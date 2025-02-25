package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDto;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningIdDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

public interface QuestioningService {

    Page<Questioning> findAll(Pageable pageable);

    Questioning findById(Long id);

    Questioning saveQuestioning(Questioning questioning);

    void deleteQuestioning(Long id);

    Set<Questioning> findByIdPartitioning(String idPartitioning);

    Optional<Questioning> findByIdPartitioningAndSurveyUnitIdSu(String idPartitioning, String surveyUnitIdSu);

    QuestioningIdDto findByCampaignIdAndSurveyUnitIdSu(String campaignId, String surveyUnitIdSu);


    /**
     * Delete questionings attached to one partitioning
     *
     * @param partitioning
     * @return nb questioning deleted
     */
    int deleteQuestioningsOfOnePartitioning(Partitioning partitioning);

    Set<Questioning> findBySurveyUnitIdSu(String idSu);

    String getAccessUrl(String role, Questioning questioning, Partitioning part);

    Page<SearchQuestioningDto> searchQuestioning(String param, Pageable pageable);

    QuestioningDetailsDto getQuestioningDetails(Long id);

    QuestionaireStatusTypeEnum getQuestioningStatus(Questioning questioning, Partitioning part);
}
