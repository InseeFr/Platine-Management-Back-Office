package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDtoImpl;

import java.util.List;

public class SearchSurveyUnitFixture {

	public static List<SearchSurveyUnitDto> generateSearches(String idSu) {
		SearchSurveyUnitDtoImpl search1 = new SearchSurveyUnitDtoImpl();
		search1.setIdSu(idSu);
		search1.setIdentificationCode("code1");
		search1.setIdentificationName("name1");
		SearchSurveyUnitDtoImpl search2 = new SearchSurveyUnitDtoImpl();
		search2.setIdSu(idSu);
		search2.setIdentificationCode("code2");
		search2.setIdentificationName("name2");

		return List.of(search1, search2);
	}
}
