package fr.insee.survey.datacollectionmanagement.query.service;

import fr.insee.survey.datacollectionmanagement.config.JSONCollectionWrapper;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogExtractionRowDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogQuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogSearchDto;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public interface MoogService {

    List<View> moogSearch(String field);

    List<MoogSearchDto> transformListViewToListMoogSearchDto(List<View> listView);

    List<MoogQuestioningEventDto> getMoogEvents(String Campaign, String idSu);

    JSONCollectionWrapper<MoogExtractionRowDto> getExtraction(String idCampaign);

    Collection<MoogExtractionRowDto> getSurveyUnitsToFollowUp(String idCampaign);

    String getReadOnlyUrl(String idCampaign, String surveyUnitId) throws NotFoundException;
}
