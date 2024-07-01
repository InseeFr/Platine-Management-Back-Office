package fr.insee.survey.datacollectionmanagement.query.service;

import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningInformationsDto;

public interface QuestioningInformationsService {
    QuestioningInformationsDto findQuestioningInformationsDtoReviewer(String idCampaign, String idsu);
    QuestioningInformationsDto findQuestioningInformationsDtoInterviewer(String idCampaign, String idsu, String contactId);

}
