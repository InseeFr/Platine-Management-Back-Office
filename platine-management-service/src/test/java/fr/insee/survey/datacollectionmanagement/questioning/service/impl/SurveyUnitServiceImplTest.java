package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnitAddress;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SurveyUnitServiceImplTest {

	private SurveyUnitRepositoryStub surveyUnitRepositoryStub;
	private SurveyUnitServiceImpl surveyUnitService;

	@BeforeEach
	void init() {
		surveyUnitRepositoryStub = new SurveyUnitRepositoryStub();
		surveyUnitService = new SurveyUnitServiceImpl(surveyUnitRepositoryStub, new SurveyUnitAddressRepositoryStub());
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


}