package fr.insee.survey.datacollectionmanagement.user.dao;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;
import fr.insee.survey.datacollectionmanagement.user.domain.*;
import fr.insee.survey.datacollectionmanagement.user.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class WalletDao {

    private final UserRepository userRepository;
    private final SourceRepository sourceRepository;
    private final SurveyUnitRepository surveyUnitRepository;
    private final GroupRepository groupRepository;
    private final GroupWalletRepository groupWalletRepository;
    private final UserWalletRepository userWalletRepository;
    private final UserGroupRepository userGroupRepository;

    @PersistenceContext
    private final EntityManager em;

    @Transactional
    public void upsertWallets(String sourceId, List<WalletDto> wallets) {
        Source source = sourceRepository.findById(sourceId)
                .orElseThrow(() -> new EntityNotFoundException("Source not found: " + sourceId));

        if (wallets == null) wallets = List.of();

        cleanData(sourceId);

        if (wallets.isEmpty()) return;

        for (WalletDto wallet : wallets) {
            insertOneWallet(source, wallet);
        }
    }


    private void cleanData(String sourceId) {
        List<GroupEntity> groups = groupRepository.findAllBySourceId(sourceId);
        userWalletRepository.deleteAllByIdSourceId(sourceId);
        groupWalletRepository.deleteAllByGroupIn(groups);
        userGroupRepository.deleteAllByGroupIn(groups);
        groupRepository.deleteAllBySourceId(sourceId);
        em.flush();
        em.clear();
    }

    private void insertOneWallet(Source source, WalletDto wallet) {
        if (StringUtils.isBlank(wallet.group()) && StringUtils.isBlank(wallet.internalUser())) {
            throw new IllegalArgumentException("At least one of the parameters 'group' or 'internaluser' must be provided.");
        }

        SurveyUnit surveyUnit = surveyUnitRepository.findById(wallet.surveyUnit())
                .orElseThrow(() -> new EntityNotFoundException("SurveyUnit not found: " + wallet.surveyUnit()));

        User user = null;
        GroupEntity group = null;

        if (StringUtils.isNotBlank(wallet.internalUser())) {
            user = userRepository.findByIdentifierIgnoreCase(wallet.internalUser())
                    .orElseThrow(() -> new EntityNotFoundException("User not found: " + wallet.internalUser()));
            UserWallet userWallet = getOrCreateUserWallet(user,surveyUnit,source);
        }

        if (StringUtils.isNotBlank(wallet.group())) {
            group = getOrCreateGroup(source, wallet.group());
            GroupWallet groupWallet = getOrCreateGroupWallet(group,surveyUnit);
        }

        if (StringUtils.isNotBlank(wallet.internalUser()) && StringUtils.isNotBlank(wallet.group())) {
            UserGroup userGroup = getOrCreateUserGroup(user,group);
        }
    }

    private UserGroup getOrCreateUserGroup(User user, GroupEntity group) {
        UserGroupId userGroupId = new UserGroupId(user.getIdentifier(), group.getGroupId());
        Optional<UserGroup> userGroup = userGroupRepository.findById(userGroupId);
        if (userGroup.isPresent()) {
            return userGroup.get();
        }
        UserGroup newUserGroup = new UserGroup();
        newUserGroup.setId(userGroupId);
        newUserGroup.setUser(user);
        newUserGroup.setGroup(group);
        return userGroupRepository.save(newUserGroup);
    }

    private GroupWallet getOrCreateGroupWallet(GroupEntity group, SurveyUnit surveyUnit) {
        GroupWalletId groupWalletId = new GroupWalletId(group.getGroupId(), surveyUnit.getIdSu());
        Optional<GroupWallet> groupWallet = groupWalletRepository.findById(groupWalletId);
        if  (groupWallet.isPresent()) {
            return groupWallet.get();
        }
        GroupWallet newGroupWallet = new GroupWallet();
        newGroupWallet.setId(groupWalletId);
        newGroupWallet.setGroup(group);
        newGroupWallet.setSurveyUnit(surveyUnit);
        return groupWalletRepository.save(newGroupWallet);
    }

    private UserWallet getOrCreateUserWallet(User user, SurveyUnit surveyUnit, Source source) {
        UserWalletId userWalletId = new UserWalletId(user.getIdentifier(), surveyUnit.getIdSu(), source.getId());
        Optional<UserWallet> userWallet = userWalletRepository.findById(userWalletId);
        if (userWallet.isPresent()) {
            return userWallet.get();
        }
        UserWallet newUserWallet = new UserWallet();
        newUserWallet.setId(userWalletId);
        newUserWallet.setUser(user);
        newUserWallet.setSurveyUnit(surveyUnit);
        newUserWallet.setSource(source);
        return userWalletRepository.save(newUserWallet);
    }

    private GroupEntity getOrCreateGroup(Source source, String groupLabel) {

        Optional<GroupEntity> existing = groupRepository.findBySource_IdAndLabel(source.getId(), groupLabel);
        if (existing.isPresent()) {
            return existing.get();
        }

        GroupEntity g = new GroupEntity();
        g.setSource(source);
        g.setLabel(groupLabel);
        return groupRepository.save(g);
    }


}
