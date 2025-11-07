package fr.insee.survey.datacollectionmanagement.user.dao;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;
import fr.insee.survey.datacollectionmanagement.user.domain.*;
import fr.insee.survey.datacollectionmanagement.user.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class WalletDao {

    private final UserRepository userRepository;

    private final SurveyUnitRepository surveyUnitRepository;
    private final GroupRepository groupRepository;
    private final GroupWalletRepository groupWalletRepository;
    private final UserWalletRepository userWalletRepository;
    private final UserGroupRepository userGroupRepository;

    @Transactional
    public void upsertWallets(Source source, List<WalletDto> wallets) {

        cleanData(source.getId());

        Set<String> userIds = wallets.stream()
                .filter(Objects::nonNull)
                .map(WalletDto::internalUser)
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        Set<String> groupLabels = wallets.stream()
                .filter(Objects::nonNull)
                .map(WalletDto::group)
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.toSet());

        Set<String> suIds = wallets.stream()
                .filter(Objects::nonNull)
                .map(WalletDto::surveyUnit)
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .collect(Collectors.toSet());

        Map<String, User> usersByKey = loadUsersByKey(userIds);
        Map<String, SurveyUnit> susById = loadSurveyUnitsById(suIds);
        Map<String, GroupEntity> groupsByLabel = loadGroupsByLabel(source.getId(), groupLabels);

        createMissingGroups(source, groupLabels, groupsByLabel);

        buildAndPersistBuffers(wallets, source, usersByKey, susById, groupsByLabel);
    }

    public void cleanData(String sourceId) {
        List<GroupEntity> groups = groupRepository.findAllBySourceId(sourceId);
        userWalletRepository.deleteAllByIdSourceId(sourceId);
        groupWalletRepository.deleteAllByGroupIn(groups);
        userGroupRepository.deleteAllByGroupIn(groups);
        groupRepository.deleteAllBySourceId(sourceId);
    }

    private Map<String, User> loadUsersByKey(Set<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }
        return userRepository.findAllByIdentifierInIgnoreCase(userIds)
                .stream()
                .collect(Collectors.toMap(
                        u -> u.getIdentifier().toLowerCase(),
                        Function.identity()
                ));
    }

    private Map<String, SurveyUnit> loadSurveyUnitsById(Set<String> suIds) {
        if (suIds == null || suIds.isEmpty()) {
            return new HashMap<>();
        }
        return surveyUnitRepository.findAllByIdSuIn(suIds)
                .stream()
                .collect(Collectors.toMap(
                        SurveyUnit::getIdSu,
                        Function.identity()
                ));
    }

    private Map<String, GroupEntity> loadGroupsByLabel(String sourceId, Set<String> groupLabels) {
        if (groupLabels == null || groupLabels.isEmpty()) {
            return new HashMap<>();
        }
        return groupRepository.findAllBySource_IdAndLabelIn(sourceId, groupLabels)
                .stream()
                .collect(Collectors.toMap(
                        GroupEntity::getLabel,
                        Function.identity()
                ));
    }

    private void createMissingGroups(
            Source source,
            Set<String> groupLabels,
            Map<String, GroupEntity> groupsByLabel
    ) {
        List<GroupEntity> groupsToCreate = new ArrayList<>();
        for (String label : groupLabels) {
            if (!groupsByLabel.containsKey(label)) {
                GroupEntity g = new GroupEntity();
                g.setSource(source);
                g.setLabel(label);
                groupsToCreate.add(g);
            }
        }
        if (!groupsToCreate.isEmpty()) {
            List<GroupEntity> saved = groupRepository.saveAll(groupsToCreate);
            for (GroupEntity g : saved) {
                groupsByLabel.put(g.getLabel(), g);
            }
        }
    }

    private void buildAndPersistBuffers(
            List<WalletDto> wallets,
            Source source,
            Map<String, User> usersByKey,
            Map<String, SurveyUnit> susById,
            Map<String, GroupEntity> groupsByLabel
    ) {
        final int BATCH = 1000;
        List<UserWallet> userWalletBuffer = new ArrayList<>(BATCH);
        List<GroupWallet> groupWalletBuffer = new ArrayList<>(BATCH);
        List<UserGroup> userGroupBuffer = new ArrayList<>(BATCH);

        Set<UserWalletId> seenUW = new HashSet<>();
        Set<GroupWalletId> seenGW = new HashSet<>();
        Set<UserGroupId> seenUG = new HashSet<>();

        for (WalletDto w : wallets) {
            if (w == null) continue;

            SurveyUnit su = susById.get(w.surveyUnit());

            User user = null;
            GroupEntity group = null;

            if (StringUtils.isNotBlank(w.internalUser())) {
                String key = w.internalUser().toLowerCase();
                user = usersByKey.get(key);
                if (user == null) {
                    throw new EntityNotFoundException("User not found: " + w.internalUser());
                }
                UserWalletId uwId = new UserWalletId(user.getIdentifier(), su.getIdSu(), source.getId());
                if (seenUW.add(uwId)) {
                    UserWallet uw = new UserWallet();
                    uw.setId(uwId);
                    uw.setUser(user);
                    uw.setSurveyUnit(su);
                    uw.setSource(source);
                    userWalletBuffer.add(uw);
                }
            }

            if (StringUtils.isNotBlank(w.group())) {
                group = groupsByLabel.get(w.group());
                if (group == null) {
                    throw new EntityNotFoundException("Group not found after creation: " + w.group());
                }
                GroupWalletId gwId = new GroupWalletId(group.getGroupId(), su.getIdSu());
                if (seenGW.add(gwId)) {
                    GroupWallet gw = new GroupWallet();
                    gw.setId(gwId);
                    gw.setGroup(group);
                    gw.setSurveyUnit(su);
                    groupWalletBuffer.add(gw);
                }
            }

            if (user != null && group != null) {
                UserGroupId ugId = new UserGroupId(user.getIdentifier(), group.getGroupId());
                if (seenUG.add(ugId)) {
                    UserGroup ug = new UserGroup();
                    ug.setId(ugId);
                    ug.setUser(user);
                    ug.setGroup(group);
                    userGroupBuffer.add(ug);
                }
            }

            if (userWalletBuffer.size() >= BATCH) {
                userWalletRepository.saveAll(userWalletBuffer);
                userWalletBuffer.clear();
            }
            if (groupWalletBuffer.size() >= BATCH) {
                groupWalletRepository.saveAll(groupWalletBuffer);
                groupWalletBuffer.clear();
            }
            if (userGroupBuffer.size() >= BATCH) {
                userGroupRepository.saveAll(userGroupBuffer);
                userGroupBuffer.clear();
            }
        }

        if (!userWalletBuffer.isEmpty()) {
            userWalletRepository.saveAll(userWalletBuffer);
        }
        if (!groupWalletBuffer.isEmpty()) {
            groupWalletRepository.saveAll(groupWalletBuffer);
        }
        if (!userGroupBuffer.isEmpty()) {
            userGroupRepository.saveAll(userGroupBuffer);
        }
    }

}
