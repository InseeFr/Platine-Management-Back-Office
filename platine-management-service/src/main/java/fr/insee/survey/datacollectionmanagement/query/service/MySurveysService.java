package fr.insee.survey.datacollectionmanagement.query.service;

import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MySurveysService {

    List<MyQuestionnaireDto> getListMyQuestionnaires(String id);
}
