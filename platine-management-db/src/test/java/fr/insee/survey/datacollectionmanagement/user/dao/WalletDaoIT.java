package fr.insee.survey.datacollectionmanagement.user.dao;

import fr.insee.survey.datacollectionmanagement.TestConfig;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;
import fr.insee.survey.datacollectionmanagement.user.domain.*;
import fr.insee.survey.datacollectionmanagement.user.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
@ContextConfiguration(classes = TestConfig.class)
@AutoConfigureTestDatabase
@Transactional
class WalletDaoIT {

    @Autowired
    WalletDao walletDao;

    @Autowired
    SourceRepository sourceRepository;
    @Autowired
    SurveyUnitRepository surveyUnitRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    UserWalletRepository userWalletRepository;
    @Autowired
    UserGroupRepository userGroupRepository;
    @Autowired
    GroupWalletRepository groupWalletRepository;

    private static final String SOURCE_ID = "SIMPSON2025";
    private static final String SOURCE_ID_2 = "SIMPSON2024";
    private static final String SURVEY_UNIT_ID = "SU-001";
    private static final String SURVEY_UNIT_ID_2 = "SU-002";
    private static final String USER_ID = "AAAAAA";
    private static final String USER_ID_2 = "BBBBBB";
    private static final String GROUP_LABEL_1 = "Groupe 1";
    private static final String GROUP_LABEL_2 = "Groupe 2";

    private Source src;
    private Source src2;

    @BeforeEach
    void setUp() {
        src = new Source();
        src.setId(SOURCE_ID);
        sourceRepository.save(src);
        src2 = new Source();
        src2.setId(SOURCE_ID_2);
        sourceRepository.save(src2);

        var su = new SurveyUnit();
        su.setIdSu(SURVEY_UNIT_ID);
        surveyUnitRepository.save(su);
        var su2 = new SurveyUnit();
        su2.setIdSu(SURVEY_UNIT_ID_2);
        surveyUnitRepository.save(su2);

        var u = new User();
        u.setIdentifier(USER_ID);
        userRepository.save(u);
        var u2 = new User();
        u2.setIdentifier(USER_ID_2);
        userRepository.save(u2);
    }

    @Test
    void insertWallets_creates_wallets_with_all_fields() {
        WalletDto w = new WalletDto(SURVEY_UNIT_ID, USER_ID, GROUP_LABEL_1);

        walletDao.insertWallets(src, List.of(w));

        Optional<GroupEntity> g = groupRepository.findBySource_IdAndLabel(SOURCE_ID, GROUP_LABEL_1);
        assertThat(g).isPresent();

        Optional<UserWallet> uw = userWalletRepository.findById(new UserWalletId(USER_ID,SURVEY_UNIT_ID,SOURCE_ID));
        assertThat(uw).isPresent();

        Optional<GroupWallet> gw = groupWalletRepository.findById(new GroupWalletId(g.get().getGroupId(), SURVEY_UNIT_ID));
        assertThat(gw).isPresent();

        Optional<UserGroup> ug = userGroupRepository.findById(new UserGroupId(USER_ID, g.get().getGroupId()));
        assertThat(ug).isPresent();
    }

    @Test
    void insertWallets_creates_wallets_without_group() {
        WalletDto w = new WalletDto(SURVEY_UNIT_ID, USER_ID, "");

        walletDao.insertWallets(src, List.of(w));

        List<GroupEntity> groups = groupRepository.findAll();
        assertThat(groups).isEmpty();

        Optional<UserWallet> uw = userWalletRepository.findById(new UserWalletId(USER_ID,SURVEY_UNIT_ID,SOURCE_ID));
        assertThat(uw).isPresent();

        List<GroupWallet> gws = groupWalletRepository.findByIdSurveyUnitId(SURVEY_UNIT_ID);
        assertThat(gws).isEmpty();

        List<UserGroup> ugs = userGroupRepository.findByIdUserId(USER_ID);
        assertThat(ugs).isEmpty();
    }

    @Test
    void insertWallets_creates_wallets_without_user() {
        WalletDto w = new WalletDto(SURVEY_UNIT_ID, "", GROUP_LABEL_1);

        walletDao.insertWallets(src, List.of(w));

        Optional<GroupEntity> g = groupRepository.findBySource_IdAndLabel(SOURCE_ID, GROUP_LABEL_1);
        assertThat(g).isPresent();

        List<UserWallet> uws = userWalletRepository.findByIdSourceIdAndIdSurveyUnitId(SOURCE_ID, SURVEY_UNIT_ID);
        assertThat(uws).isEmpty();

        Optional<GroupWallet> gw = groupWalletRepository.findById(new GroupWalletId(g.get().getGroupId(), SURVEY_UNIT_ID));
        assertThat(gw).isPresent();

        List<UserGroup> ugs = userGroupRepository.findByIdGroupId(g.get().getGroupId());
        assertThat(ugs).isEmpty();
    }

    @Test
    void insertWallets_is_idempotent_on_same_input() {
        WalletDto w = new WalletDto(SURVEY_UNIT_ID, USER_ID, GROUP_LABEL_1);

        walletDao.insertWallets(src, List.of(w));
        walletDao.insertWallets(src, List.of(w));

        assertThat(userWalletRepository.count()).isEqualTo(1);
        assertThat(groupRepository.count()).isEqualTo(1);
        assertThat(groupWalletRepository.count()).isEqualTo(1);
        assertThat(userGroupRepository.count()).isEqualTo(1);
    }

    @Test
    void cleanDataTest() {
        WalletDto w = new WalletDto(SURVEY_UNIT_ID, USER_ID, GROUP_LABEL_1);
        WalletDto w2 = new WalletDto(SURVEY_UNIT_ID, USER_ID_2, GROUP_LABEL_1);

        walletDao.insertWallets(src, List.of(w,w2));
        assertThat(userWalletRepository.count()).isEqualTo(2);
        assertThat(groupRepository.count()).isEqualTo(1);
        assertThat(groupWalletRepository.count()).isEqualTo(1);
        assertThat(userGroupRepository.count()).isEqualTo(2);

        walletDao.cleanData(SOURCE_ID);
        assertThat(userWalletRepository.count()).isZero();
        assertThat(groupRepository.count()).isZero();
        assertThat(groupWalletRepository.count()).isZero();
        assertThat(userGroupRepository.count()).isZero();
    }

    @Test
    void insertWallets_create_many_wallets() {

        WalletDto w1  = new WalletDto(SURVEY_UNIT_ID,     USER_ID,    null);
        WalletDto w2  = new WalletDto(SURVEY_UNIT_ID,     USER_ID_2,  null);
        WalletDto w3  = new WalletDto(SURVEY_UNIT_ID_2,   USER_ID,    null);
        WalletDto w4  = new WalletDto(SURVEY_UNIT_ID_2,   USER_ID_2,  null);

        WalletDto w5  = new WalletDto(SURVEY_UNIT_ID,     null,       GROUP_LABEL_1);
        WalletDto w6  = new WalletDto(SURVEY_UNIT_ID,     null,       GROUP_LABEL_2);
        WalletDto w7  = new WalletDto(SURVEY_UNIT_ID_2,   null,       GROUP_LABEL_1);
        WalletDto w8  = new WalletDto(SURVEY_UNIT_ID_2,   null,       GROUP_LABEL_2);

        WalletDto w9  = new WalletDto(SURVEY_UNIT_ID,     USER_ID,    GROUP_LABEL_1);
        WalletDto w10 = new WalletDto(SURVEY_UNIT_ID,     USER_ID,    GROUP_LABEL_2);
        WalletDto w11 = new WalletDto(SURVEY_UNIT_ID,     USER_ID_2,  GROUP_LABEL_1);
        WalletDto w12 = new WalletDto(SURVEY_UNIT_ID,     USER_ID_2,  GROUP_LABEL_2);
        WalletDto w13 = new WalletDto(SURVEY_UNIT_ID_2,   USER_ID,    GROUP_LABEL_1);
        WalletDto w14 = new WalletDto(SURVEY_UNIT_ID_2,   USER_ID,    GROUP_LABEL_2);
        WalletDto w15 = new WalletDto(SURVEY_UNIT_ID_2,   USER_ID_2,  GROUP_LABEL_1);
        WalletDto w16 = new WalletDto(SURVEY_UNIT_ID_2,   USER_ID_2,  GROUP_LABEL_2);

        walletDao.insertWallets(
                src,
                List.of(w1,w2,w3,w4,w5,w6,w7,w8,w9,w10,w11,w12,w13,w14,w15,w16)
        );

        WalletDto w17 = new WalletDto(SURVEY_UNIT_ID,     USER_ID,    null);
        WalletDto w18 = new WalletDto(SURVEY_UNIT_ID,     USER_ID_2,  null);
        WalletDto w19 = new WalletDto(SURVEY_UNIT_ID_2,   USER_ID,    null);
        WalletDto w20 = new WalletDto(SURVEY_UNIT_ID_2,   USER_ID_2,  null);

        WalletDto w21 = new WalletDto(SURVEY_UNIT_ID,     null,       GROUP_LABEL_1);
        WalletDto w22 = new WalletDto(SURVEY_UNIT_ID,     null,       GROUP_LABEL_2);
        WalletDto w23 = new WalletDto(SURVEY_UNIT_ID_2,   null,       GROUP_LABEL_1);
        WalletDto w24 = new WalletDto(SURVEY_UNIT_ID_2,   null,       GROUP_LABEL_2);

        WalletDto w25 = new WalletDto(SURVEY_UNIT_ID,     USER_ID,    GROUP_LABEL_1);
        WalletDto w26 = new WalletDto(SURVEY_UNIT_ID,     USER_ID,    GROUP_LABEL_2);
        WalletDto w27 = new WalletDto(SURVEY_UNIT_ID,     USER_ID_2,  GROUP_LABEL_1);
        WalletDto w28 = new WalletDto(SURVEY_UNIT_ID,     USER_ID_2,  GROUP_LABEL_2);
        WalletDto w29 = new WalletDto(SURVEY_UNIT_ID_2,   USER_ID,    GROUP_LABEL_1);
        WalletDto w30 = new WalletDto(SURVEY_UNIT_ID_2,   USER_ID,    GROUP_LABEL_2);
        WalletDto w31 = new WalletDto(SURVEY_UNIT_ID_2,   USER_ID_2,  GROUP_LABEL_1);
        WalletDto w32 = new WalletDto(SURVEY_UNIT_ID_2,   USER_ID_2,  GROUP_LABEL_2);

        walletDao.insertWallets(
                src2,
                List.of(w17,w18,w19,w20,w21,w22,w23,w24,w25,w26,w27,w28,w29,w30,w31,w32)
        );

        assertThat(userWalletRepository.count()).isEqualTo(8);
        assertThat(groupRepository.count()).isEqualTo(4);
        assertThat(groupWalletRepository.count()).isEqualTo(8);
        assertThat(userGroupRepository.count()).isEqualTo(8);
    }

}