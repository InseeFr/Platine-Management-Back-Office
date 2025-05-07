package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchSurveyUnitContactDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnitAddress;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ContactAccreditedToSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.ContactRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.SearchSurveyUnitFixture;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.SurveyUnitAddressRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.SurveyUnitRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SurveyUnitServiceImplTest {

    private SurveyUnitRepositoryStub surveyUnitRepositoryStub;
    private SurveyUnitServiceImpl surveyUnitService;
    private ContactRepositoryStub contactRepositoryStub;

    @BeforeEach
    void init() {
        contactRepositoryStub = new ContactRepositoryStub();
        surveyUnitRepositoryStub = new SurveyUnitRepositoryStub();
        SurveyUnitAddressRepositoryStub surveyUnitAddressRepositoryStub = new SurveyUnitAddressRepositoryStub();
        surveyUnitService = new SurveyUnitServiceImpl(surveyUnitRepositoryStub, surveyUnitAddressRepositoryStub, contactRepositoryStub);
    }

    @Test
    @DisplayName("Should return a SurveyUnit")
    void findbyId() {
        //given
        SurveyUnit surveyUnit = new SurveyUnit();
        surveyUnit.setIdSu("testId");
        surveyUnitRepositoryStub.setSurveyUnit(surveyUnit);
        // when
        SurveyUnit result = surveyUnitService.findbyId("testId");

        //then
        assertThat(result).isNotNull().isEqualTo(surveyUnit);
    }

    @Test
    @DisplayName("Should throw when surveyUnit not present")
    void findbyId_should_throw_not_found_when_id_not_found() {
        //given
        surveyUnitRepositoryStub.setShouldThrow(true);

        //when and then
        assertThatThrownBy(() -> surveyUnitService.findbyId("testId")).isInstanceOf(NotFoundException.class).hasMessage("SurveyUnit testId not found");
    }


    @Test
    @DisplayName("Should save survey unit if not already present in repository")
    void saveSurveyUnitddress_when_survey_unit_does_not_exist_and_has_no_address() {
        //given
        SurveyUnit surveyUnit = new SurveyUnit();
        surveyUnit.setIdSu("id1");
        surveyUnitRepositoryStub.setShouldThrow(true);

        //when
        SurveyUnit result = surveyUnitService.saveSurveyUnitAndAddress(surveyUnit);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getIdSu()).isEqualTo("id1");
    }

    @Test
    @DisplayName("Should update survey unit address with existing survey unit's address in repo")
    void saveSurveyUnitddress_when_survey_unit_has_no_address_and_exists_with_address_in_repo() {
        //given
        SurveyUnit surveyUnitWithAddress = new SurveyUnit();
        surveyUnitWithAddress.setIdSu("id1");
        SurveyUnitAddress surveyUnitAddressInRepo = new SurveyUnitAddress();
        surveyUnitAddressInRepo.setId(2L);
        surveyUnitWithAddress.setSurveyUnitAddress(surveyUnitAddressInRepo);

        surveyUnitRepositoryStub.setSurveyUnit(surveyUnitWithAddress);

        SurveyUnit surveyUnit = new SurveyUnit();
        surveyUnit.setIdSu("id1");
        SurveyUnitAddress surveyUnitAddress = new SurveyUnitAddress();
        surveyUnitAddress.setId(1L);
        surveyUnit.setSurveyUnitAddress(surveyUnitAddress);

        //when
        SurveyUnit result = surveyUnitService.saveSurveyUnitAndAddress(surveyUnit);

        //then
        assertThat(result.getSurveyUnitAddress()).extracting("id").isEqualTo(2L);
    }

    @Test
    @DisplayName("Should persist survey unit address when survey unit exists in repo without address")
    void saveSurveyUnitddress_when_survey_unit_has_address_and_exists_without_address_in_repo() {
        //given
        SurveyUnit surveyUnitWithAddress = new SurveyUnit();
        surveyUnitWithAddress.setIdSu("id1");

        surveyUnitRepositoryStub.setSurveyUnit(surveyUnitWithAddress);

        SurveyUnit surveyUnit = new SurveyUnit();
        surveyUnit.setIdSu("id1");
        SurveyUnitAddress surveyUnitAddress = new SurveyUnitAddress();
        surveyUnitAddress.setId(1L);
        surveyUnit.setSurveyUnitAddress(surveyUnitAddress);

        //when
        SurveyUnit result = surveyUnitService.saveSurveyUnitAndAddress(surveyUnit);

        //then
        assertThat(result.getSurveyUnitAddress()).extracting("id").isEqualTo(1L);
    }


    @Test
    @DisplayName("findbyIdentifier")
    void findbyIdentifier() {
        //given
        surveyUnitRepositoryStub.setEchoes(SearchSurveyUnitFixture.generateSearches("testId"));
        Pageable pageable = PageRequest.of(0, 2);
        //when
        Page<SearchSurveyUnitDto> result = surveyUnitService.findbyIdentifier("testId", pageable);
        //then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result).first().extracting("idSu").isEqualTo("testId");
    }

    @Test
    @DisplayName("findbyIdentificationCode")
    void findbyIdentificationCode() {
        //given
        surveyUnitRepositoryStub.setEchoes(SearchSurveyUnitFixture.generateSearches("testId"));
        Pageable pageable = PageRequest.of(0, 2);
        //when
        Page<SearchSurveyUnitDto> result = surveyUnitService.findbyIdentificationCode("code1", pageable);
        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result).first().extracting("identificationCode").isEqualTo("code1");

    }

    @Test
    @DisplayName("findbyIdentificationName")
    void findbyIdentificationName() {
        //given
        surveyUnitRepositoryStub.setEchoes(SearchSurveyUnitFixture.generateSearches("testId"));
        Pageable pageable = PageRequest.of(0, 2);
        //when
        Page<SearchSurveyUnitDto> result = surveyUnitService.findbyIdentificationName("name1", pageable);
        //then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result).first().extracting("identificationName").isEqualTo("name1");

    }

    @Test
    @DisplayName("findAll")
    void findAll() {
        //given
        surveyUnitRepositoryStub.setSurveyUnits(
                List.of(
                        new SurveyUnit(), new SurveyUnit(), new SurveyUnit()
                )
        );
        Pageable pageable = PageRequest.of(0, 2);

        //when
        Page<SurveyUnit> result = surveyUnitService.findAll(pageable);
        //then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("saveSurveyUnit")
    void saveSurveyUnit() {
        //given
        SurveyUnit surveyUnit = new SurveyUnit();
        surveyUnit.setIdSu("testId");
        //when
        SurveyUnit result = surveyUnitService.saveSurveyUnit(surveyUnit);
        //then
        assertThat(result).isNotNull().extracting("idSu").isEqualTo("testId");
    }

    @Test
    @DisplayName("deleteSurveyUnit")
    void deleteSurveyUnit() {
        //given
        SurveyUnit surveyUnit = new SurveyUnit();
        surveyUnit.setIdSu("testId");
        surveyUnitRepositoryStub.setSurveyUnit(surveyUnit);
        //when
        surveyUnitService.deleteSurveyUnit("testId");
        SurveyUnit repoSurveyUnit = surveyUnitRepositoryStub.getSurveyUnit();
        //then
        assertThat(repoSurveyUnit).isNull();
    }

    @Test
    @DisplayName("Should return empty list when no contacts are found for survey unit ID")
    void findContactsBySurveyUnitId_shouldReturnEmptyList_whenNoContactsFound() {
        // Given
        String surveyUnitId = "SU123";
        surveyUnitRepositoryStub.setListContactAccreditedToSurveyUnitDto(List.of());

        // When
        List<SearchSurveyUnitContactDto> result = surveyUnitService.findContactsBySurveyUnitId(surveyUnitId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return contact details with campaigns when contacts exist")
    void findContactsBySurveyUnitId_shouldReturnContactsWithCampaigns() {
        // Given
        String surveyUnitId = "SU123";
        String contactId = "C1";

        Contact contact = new Contact();
        contact.setIdentifier(contactId);
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setEmail("john.doe@example.com");
        Address address = new Address();
        address.setCityName("San Francisco");
        contact.setAddress(address);
        contact.setPhone("123456789");

        List<ContactAccreditedToSurveyUnitDto> contactAccreditedToSurveyUnitList =
                List.of(new ContactAccreditedToSurveyUnitDto(contactId,true, "Campaign1,Campaign2"));

        contactRepositoryStub.setContacts(List.of(contact));
        surveyUnitRepositoryStub.setListContactAccreditedToSurveyUnitDto(contactAccreditedToSurveyUnitList);

        // When
        List<SearchSurveyUnitContactDto> result = surveyUnitService.findContactsBySurveyUnitId(surveyUnitId);

        // Then
        assertThat(result).hasSize(1);
        SearchSurveyUnitContactDto dto = result.getFirst();
        assertThat(dto.getIdentifier()).isEqualTo(contactId);
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getLastName()).isEqualTo("Doe");
        assertThat(dto.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(dto.getPhoneNumber()).isEqualTo("123456789");
        assertThat(dto.isMain()).isTrue();
        assertThat(dto.getCampaigns()).containsExactlyInAnyOrder("Campaign1", "Campaign2");
    }

    @Test
    @DisplayName("Should not return contacts even no campaigns are found")
    void findContactsBySurveyUnitId_shouldNotReturnContacts_whenNoCampaignsFound() {
        // Given
        String surveyUnitId = "SU123";
        String contactId = "C1";

        Contact contact = new Contact();
        contact.setIdentifier(contactId);
        contact.setFirstName("Alice");
        contact.setLastName("Smith");

        contactRepositoryStub.setContacts(List.of(contact));
        surveyUnitRepositoryStub.setListContactAccreditedToSurveyUnitDto(List.of());


        // When
        List<SearchSurveyUnitContactDto> result = surveyUnitService.findContactsBySurveyUnitId(surveyUnitId);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return multiple contact lines  for the same contact when accredited according to main character")
    void findContactsBySurveyUnitId_shouldReturnMultipleDtos_forSameContact() {
        // Given
        String surveyUnitId = "SU123";
        String contactId = "C1";

        // Mock contact entity
        Contact contact = new Contact();
        contact.setIdentifier(contactId);
        contact.setFirstName("John");
        contact.setLastName("Doe");
        contact.setFunction("Manager");

        // Simulate multiple accreditations
        ContactAccreditedToSurveyUnitDto accreditationMain = new ContactAccreditedToSurveyUnitDto(
                contactId, true, "Campaign1"
        );
        ContactAccreditedToSurveyUnitDto accreditationNotMain = new ContactAccreditedToSurveyUnitDto(
                contactId, false, "Campaign2"
        );

        contactRepositoryStub.setContacts(List.of(contact));
        surveyUnitRepositoryStub.setListContactAccreditedToSurveyUnitDto(List.of(accreditationMain, accreditationNotMain));

        contactRepositoryStub.setContacts(List.of(contact));


        // When
        List<SearchSurveyUnitContactDto> result = surveyUnitService.findContactsBySurveyUnitId(surveyUnitId);

        // Then
        assertThat(result).hasSize(2);

        // Check contents of each DTO
        SearchSurveyUnitContactDto dto1 = result.get(0);
        SearchSurveyUnitContactDto dto2 = result.get(1);

        assertThat(dto1.getCampaigns()).containsExactlyInAnyOrder("Campaign1");
        assertThat(dto2.getCampaigns()).containsExactlyInAnyOrder("Campaign2");

        assertThat(dto1.isMain()).isTrue();
        assertThat(dto2.isMain()).isFalse();

        // Optional: check both DTOs have same contact info
        assertThat(dto1.getIdentifier()).isEqualTo(contactId);
        assertThat(dto2.getIdentifier()).isEqualTo(contactId);
    }

}