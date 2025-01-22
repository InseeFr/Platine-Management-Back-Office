package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnitComment;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitAddressRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.SurveyUnitRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SurveyUnitServiceImplTest {

	private SurveyUnitRepositoryStub surveyUnitRepositoryStub;
	private SurveyUnitAddressRepository surveyUnitAddressRepository = null;

	private SurveyUnitServiceImpl surveyUnitService;

	@BeforeEach
	void init() {
		surveyUnitRepositoryStub = new SurveyUnitRepositoryStub();
		surveyUnitService = new SurveyUnitServiceImpl(surveyUnitRepositoryStub, surveyUnitAddressRepository);
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
	void saveSurveyUnitddressComments_when_survey_unit_does_not_exist_and_has_no_address() {
		//given
		SurveyUnit surveyUnit = new SurveyUnit();
		surveyUnit.setIdSu("id1");
		surveyUnitRepositoryStub.setShouldThrow(true);

		//when
		SurveyUnit result = surveyUnitService.saveSurveyUnitAddressComments(surveyUnit);

		//then
		assertThat(result).isNotNull();
		assertThat(result.getIdSu()).isEqualTo("id1");
	}

	@Test
	void saveSurveyUnitddressComments_when_survey_unit_exists_and_has_no_address() {
		//given
		SurveyUnit surveyUnitWithComments = new SurveyUnit();
		surveyUnitWithComments.setIdSu("id1");
		SurveyUnitComment comment1 = new SurveyUnitComment();
		comment1.setId(1L);
		surveyUnitWithComments.setSurveyUnitComments(Set.of(comment1));
		surveyUnitRepositoryStub.setSurveyUnit(surveyUnitWithComments);

		SurveyUnit surveyUnit = new SurveyUnit();
		surveyUnit.setIdSu("id1");

		//when
		SurveyUnit result = surveyUnitService.saveSurveyUnitAddressComments(surveyUnit);

		//then
		assertThat(result.getSurveyUnitComments())
				.hasSize(1);
		assertThat(result.getSurveyUnitComments()).singleElement().extracting("id").isEqualTo(1L);
	}

}