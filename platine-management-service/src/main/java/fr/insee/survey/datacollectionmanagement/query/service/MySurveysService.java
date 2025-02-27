package fr.insee.survey.datacollectionmanagement.query.service;

import java.util.List;

import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDto;
import org.springframework.stereotype.Service;

import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestioningDto;

@Service
public interface MySurveysService {

    List<MyQuestioningDto> getListMySurveys(String id);

    List<MyQuestionnaireDto> getListMyQuestionnaires(String id, String QuestionnaireApiUrl);
}
