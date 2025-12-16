package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.dto.QuestioningCsvDto;
import fr.insee.survey.datacollectionmanagement.query.dto.AssistanceDto;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDto;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.questioning.InterrogationPriorityInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningIdDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningProbationDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchQuestioningParams;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.*;

public interface QuestioningService {

    Page<Questioning> findAll(Pageable pageable);

    Questioning findById(UUID id);

    Questioning saveQuestioning(Questioning questioning);

    QuestioningProbationDto updateQuestioningProbation(QuestioningProbationDto questioningProbationDto);

    List<QuestioningCsvDto> getQuestioningsByCampaignIdForCsv(String campaignId);

    void deleteQuestioning(UUID id);

    Set<Questioning> findByIdPartitioning(String idPartitioning);

    Optional<Questioning> findByIdPartitioningAndSurveyUnitIdSu(String idPartitioning, String surveyUnitIdSu);

    QuestioningIdDto findByCampaignIdAndSurveyUnitIdSu(String campaignId, String surveyUnitIdSu);

    AssistanceDto getMailAssistanceDto(UUID questioningId) ;
    /**
     * Delete questionings attached to one partitioning
     *
     * @param partitioning the partitioning to search
     * @return nb questioning deleted
     */
    int deleteQuestioningsOfOnePartitioning(Partitioning partitioning);

    Set<Questioning> findBySurveyUnitIdSu(String idSu);

    Slice<SearchQuestioningDto> searchQuestionings(SearchQuestioningParams searchQuestioningParams, Pageable pageable, String userId);

    QuestioningDetailsDto getQuestioningDetails(UUID id);

    QuestionnaireStatusTypeEnum getQuestioningStatusFileUpload(Date openingDate, Date closingDate);
  
    QuestionnaireStatusTypeEnum getQuestioningStatus(UUID questioningId, Date openingDate, Date closingDate);

    boolean hasExpertiseStatus(UUID questioningId);

    void updatePriorities(List<InterrogationPriorityInputDto> priorities);

    Set<UUID> findMissingIds(Set<UUID> ids);

    /**
     * Indicates whether the given questioning data can be exported to pdf
     * @param questioningId the questioning identifier
     * @return {@code true} if the questioning data can be exported, {@code false} otherwise
     */
    boolean canExportQuestioningDataToPdf(UUID questioningId);
}
