package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.dto.QuestioningCsvDto;
import fr.insee.survey.datacollectionmanagement.query.dto.AssistanceDto;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDto;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningIdDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchQuestioningParams;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface QuestioningService {

    Page<Questioning> findAll(Pageable pageable);

    Questioning findById(UUID id);

    Questioning saveQuestioning(Questioning questioning);

    List<QuestioningCsvDto> getQuestioningsByCampaignIdForCsv(String campaignId);

    void deleteQuestioning(UUID id);

    Set<Questioning> findByIdPartitioning(String idPartitioning);

    Optional<Questioning> findByIdPartitioningAndSurveyUnitIdSu(String idPartitioning, String surveyUnitIdSu);

    QuestioningIdDto findByCampaignIdAndSurveyUnitIdSu(String campaignId, String surveyUnitIdSu);

    AssistanceDto getMailAssistanceDto(UUID questioningId) ;
    /**
     * Delete questionings attached to one partitioning
     *
     * @param partitioning
     * @return nb questioning deleted
     */
    int deleteQuestioningsOfOnePartitioning(Partitioning partitioning);

    Set<Questioning> findBySurveyUnitIdSu(String idSu);

    Slice<SearchQuestioningDto> searchQuestionings(SearchQuestioningParams searchQuestioningParams, Pageable pageable);

    QuestioningDetailsDto getQuestioningDetails(UUID id);

    QuestionnaireStatusTypeEnum getQuestioningStatusFileUpload(Date openingDate, Date closingDate);
  
    QuestionnaireStatusTypeEnum getQuestioningStatus(UUID questioningId, Date openingDate, Date closingDate);

    boolean hasExpertiseStatus(UUID questioningId);
}
