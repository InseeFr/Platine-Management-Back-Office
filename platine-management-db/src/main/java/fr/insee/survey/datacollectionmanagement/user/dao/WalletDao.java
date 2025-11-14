package fr.insee.survey.datacollectionmanagement.user.dao;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;
import fr.insee.survey.datacollectionmanagement.user.domain.*;
import fr.insee.survey.datacollectionmanagement.user.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Transactional
@Slf4j
public class WalletDao {

    private final UserRepository userRepository;

    private final SurveyUnitRepository surveyUnitRepository;
    private final GroupRepository groupRepository;
    private final GroupWalletRepository groupWalletRepository;
    private final UserWalletRepository userWalletRepository;
    private final UserGroupRepository userGroupRepository;

    private static final int BATCH = 1000;

    public void insertWallets(Source source, List<WalletDto> wallets) {

        Set<String> userIds = wallets.stream()
                .filter(Objects::nonNull)
                .map(WalletDto::internalUser)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        Set<String> groupLabels = wallets.stream()
                .filter(Objects::nonNull)
                .map(WalletDto::group)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        Set<String> suIds = wallets.stream()
                .filter(Objects::nonNull)
                .map(WalletDto::surveyUnit)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());

        Map<String, User> usersByKey = loadUsersByKey(userIds);
        Map<String, SurveyUnit> susById = loadSurveyUnitsById(suIds);
        Map<String, GroupEntity> groupsByLabel = loadGroupsByLabel(source.getId(), groupLabels);

        createGroups(source, groupLabels, groupsByLabel);

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
                        u -> u.getIdentifier().toUpperCase(),
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

    private void createGroups(
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

        List<UserWallet> userWalletBuffer = new ArrayList<>(BATCH);
        List<GroupWallet> groupWalletBuffer = new ArrayList<>(BATCH);
        List<UserGroup> userGroupBuffer = new ArrayList<>(BATCH);

        Set<UserWalletId> seenUW = new HashSet<>();
        Set<GroupWalletId> seenGW = new HashSet<>();
        Set<UserGroupId> seenUG = new HashSet<>();

        for (WalletDto w : wallets) {
            if (w == null) continue;

            SurveyUnit su = susById.get(w.surveyUnit());

            User user =  usersByKey.getOrDefault(w.internalUser(), null);
            GroupEntity group = groupsByLabel.getOrDefault(w.group(), null);

            handleUserWallet(su, source, user, seenUW, userWalletBuffer);
            handleGroupWallet(su, group, seenGW, groupWalletBuffer);
            handleUserGroup(user, group, seenUG, userGroupBuffer);

            flushIfNeeded(userWalletBuffer, userWalletRepository::saveAll);
            flushIfNeeded(groupWalletBuffer, groupWalletRepository::saveAll);
            flushIfNeeded(userGroupBuffer, userGroupRepository::saveAll);
        }

        flushRemaining(userWalletBuffer, userWalletRepository::saveAll);
        flushRemaining(groupWalletBuffer, groupWalletRepository::saveAll);
        flushRemaining(userGroupBuffer, userGroupRepository::saveAll);
    }

    private void handleUserWallet(
            SurveyUnit su,
            Source source,
            User user,
            Set<UserWalletId> seenUW,
            List<UserWallet> userWalletBuffer
    ) {
        if (user == null) return;

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

    private void handleGroupWallet(
            SurveyUnit su,
            GroupEntity group,
            Set<GroupWalletId> seenGW,
            List<GroupWallet> groupWalletBuffer
    ) {
        if (group == null) return;

        GroupWalletId gwId = new GroupWalletId(group.getGroupId(), su.getIdSu());
        if (seenGW.add(gwId)) {
            GroupWallet gw = new GroupWallet();
            gw.setId(gwId);
            gw.setGroup(group);
            gw.setSurveyUnit(su);
            groupWalletBuffer.add(gw);
        }
    }

    private void handleUserGroup(
            User user,
            GroupEntity group,
            Set<UserGroupId> seenUG,
            List<UserGroup> userGroupBuffer
    ) {
        if (user == null || group == null) return;

        UserGroupId ugId = new UserGroupId(user.getIdentifier(), group.getGroupId());
        if (seenUG.add(ugId)) {
            UserGroup ug = new UserGroup();
            ug.setId(ugId);
            ug.setUser(user);
            ug.setGroup(group);
            userGroupBuffer.add(ug);
        }
    }

    private <T> void flushIfNeeded(List<T> buffer, Consumer<List<T>> saver) {
        if (buffer.size() >= BATCH) {
            saver.accept(buffer);
            buffer.clear();
        }
    }

    private <T> void flushRemaining(List<T> buffer, Consumer<List<T>> saver) {
        if (!buffer.isEmpty()) {
            saver.accept(buffer);
        }
    }

}
