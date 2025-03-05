package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.dto.BusinessContactDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.BusinessContactsDto;
import fr.insee.survey.datacollectionmanagement.contact.enums.GenderEnum;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BusinessContactServiceImplTest {

    private final static String ID_CAMPAIGN1 = "ID_CAMPAIGN1";
    private final static String ID_PARTITION1 = "ID_PARTITION1";
    private final static String ID_PARTITION2 = "ID_PARTITION2";
    private final static String ID_PARTITION3 = "ID_PARTITION3";

    private final static String ID_SU1 = "ID_SU1";
    private final static String ID_CONTAC1 = "ID_CONTAC1";
    private final static String ID_CONTAC2 = "ID_CONTAC2";
    private final static String ID_CONTAC3 = "ID_CONTAC3";
    private final static String ID_CONTAC4 = "ID_CONTAC4";
    private final static String ID_CONTAC5 = "ID_CONTAC5";
    @Mock
    QuestioningService questioningService;
    @Mock
    CampaignService campaignService;
    @Mock
    ContactService contactService;
    @InjectMocks
    BusinessContactServiceImpl service;

    static Stream<Arguments> findMainContactByCampaignAndSurveyUnitSource() {
        return Stream.of(
                //First test: 2 partitions
                Arguments.of(
                        List.of(
                                new PartitionConfig(
                                        ID_PARTITION1,
                                        List.of(
                                                new ContactConfig(ID_CONTAC1, true, GenderEnum.Undefined, true),
                                                new ContactConfig(ID_CONTAC2, false, GenderEnum.Female, false)
                                        ))
                                ,
                                new PartitionConfig(
                                        ID_PARTITION2,
                                        List.of(
                                                new ContactConfig(ID_CONTAC3, true, GenderEnum.Female, true),
                                                new ContactConfig(ID_CONTAC4, false, GenderEnum.Female, false),
                                                new ContactConfig(ID_CONTAC5, true, GenderEnum.Female, false)
                                        ))
                        )
                        ,
                        //exepect Contacts to be returned
                        List.of(ID_CONTAC3,ID_CONTAC5,ID_CONTAC1)
                ),
                //Second test:  1 partition
                Arguments.of(
                        List.of(
                                new PartitionConfig(
                                        ID_PARTITION1,
                                        List.of(
                                                new ContactConfig(ID_CONTAC1, false, GenderEnum.Male, true),
                                                new ContactConfig(ID_CONTAC2, true, GenderEnum.Female, false)
                                        ))
                        )
                        ,
                        //exepect Contacts to be returned
                        List.of(ID_CONTAC2)
                ),
                //Third test:  no partition
                Arguments.of(
                        List.of(  )
                        ,
                        //exepect Contacts to be returned
                        List.of()
                ),
                //Forth test:  no contact on the su in one partition ; one partition without main
                Arguments.of(
                        List.of(
                                new PartitionConfig(
                                        ID_PARTITION1,
                                        List.of(
                                                new ContactConfig(ID_CONTAC1, true, GenderEnum.Undefined, true),
                                                new ContactConfig(ID_CONTAC2, false, GenderEnum.Female, false)
                                        ))
                                ,
                                new PartitionConfig(
                                        ID_PARTITION2,
                                        List.of(
                                        ))
                                ,
                                new PartitionConfig(
                                        ID_PARTITION3,
                                        List.of(
                                                new ContactConfig(ID_CONTAC3, false, GenderEnum.Undefined, true)
                                        ))
                        )
                        ,
                        //exepect Contacts to be returned
                        List.of(ID_CONTAC1)
                )
        );
    }

    private static Campaign createCampaign(String id, Set<Partitioning> partitions) {
        Campaign campaign = new Campaign();
        campaign.setId(id);
        campaign.setPartitionings(partitions);
        campaign.getPartitionings().stream().forEach(partitioning -> partitioning.setCampaign(campaign));
        return campaign;
    }

    private static Questioning createQuestioning(Set<QuestioningAccreditation> questioningAccreditations) {
        Questioning questioning = new Questioning();
        questioning.setQuestioningAccreditations(questioningAccreditations);
        return questioning;
    }

    private static QuestioningAccreditation createAccreditation(String idContact, boolean isMain) {
        QuestioningAccreditation accreditation = new QuestioningAccreditation();
        accreditation.setMain(isMain);
        accreditation.setIdContact(idContact);
        return accreditation;
    }

    private static Contact createContact(String idContact, boolean createAddress, GenderEnum gender) {
        Contact res = new Contact();
        res.setIdentifier(idContact);
        res.setGender(gender);
        long i = 0L;
        if (createAddress) {
            Address address = createDefaultAddress();
            address.setId(++i);
            res.setAddress(address);
        }
        return res;
    }

    private static Address createDefaultAddress() {
        Address address = new Address();
        address.setStreetNumber("1");
        address.setRepetitionIndex(null);
        address.setStreetType("Rue");
        address.setStreetName("Exemple");
        address.setAddressSupplement("Apt 101");
        address.setCityName("Paris");
        address.setZipCode("75001");
        address.setCedexCode(null);
        address.setCedexName(null);
        address.setSpecialDistribution(null);
        address.setCountryCode("FR");
        address.setCountryName("France");
        return address;
    }

    @ParameterizedTest
    @MethodSource("findMainContactByCampaignAndSurveyUnitSource")
    @DisplayName("findMainContactByCampaignAndSurveyUnit should work for various inputs")
    void findMainContactByCampaignAndSurveyUnit(List<PartitionConfig> partitionConfigs, List<String> expectedMainContactIds) {
        // Given
        Set<Partitioning> partitionings = new HashSet<>();
        for(PartitionConfig partitionConfig:partitionConfigs){
            Partitioning part = new Partitioning();
            part.setId(partitionConfig.idPartition);
            partitionings.add(part);

            Set<QuestioningAccreditation> accreditations = new HashSet<>();
            for (ContactConfig contactConfig : partitionConfig.contacts){
                QuestioningAccreditation accreditation = createAccreditation(contactConfig.idContact, contactConfig.isMain);
                accreditations.add(accreditation);

                //add the contact to the list of known contacts
                var contact = createContact(contactConfig.idContact,contactConfig.hasAdress,contactConfig.gender);
                lenient().when(contactService.findByIdentifier(contactConfig.idContact)).thenReturn(contact);

            }
            Questioning questioning = createQuestioning(accreditations);
            when(questioningService.findByIdPartitioningAndSurveyUnitIdSu(partitionConfig.idPartition, ID_SU1))
                    .thenReturn(Optional.of(questioning));

        }
        when(campaignService.findById(ID_CAMPAIGN1)).thenReturn(createCampaign(ID_CAMPAIGN1, partitionings));


        //When
        BusinessContactsDto resultBusinessContactDto = service.findMainContactByCampaignAndSurveyUnit(ID_CAMPAIGN1, ID_SU1);

        //Then
        assertThat(resultBusinessContactDto).isNotNull();
        assertThat(resultBusinessContactDto.getBusinessContactDtoList())
                .extracting(BusinessContactDto::getIdeC)
                .containsExactlyInAnyOrderElementsOf(expectedMainContactIds);
    }


    record PartitionConfig(String idPartition, List<ContactConfig> contacts) {
    }

    record ContactConfig(String idContact, boolean isMain, GenderEnum gender, boolean hasAdress) {
    }
}