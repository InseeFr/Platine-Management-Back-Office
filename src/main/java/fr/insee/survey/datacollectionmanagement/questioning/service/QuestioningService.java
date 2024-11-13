package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface QuestioningService {

    public Page<Questioning> findAll(Pageable pageable);

    public Questioning findbyId(Long id);

    public Questioning saveQuestioning(Questioning questioning);

    public void deleteQuestioning(Long id);

    public Set<Questioning> findByIdPartitioning(String idPartitioning);

    public Questioning findByIdPartitioningAndSurveyUnitIdSu(String idPartitioning, String surveyUnitIdSu);

    /**
     * Delete questionings attached to one partitioning
     *
     * @param partitioning
     * @return nb questioning deleted
     */
    public int deleteQuestioningsOfOnePartitioning(Partitioning partitioning);

    public Set<Questioning> findBySurveyUnitIdSu(String idSu);

    public String getAccessUrl(String baseUrl, String typeUrl, String role, Questioning questioning, String surveyUnitId, String sourceId);

    Page<SearchQuestioningDto> searchQuestioning(String param, Pageable pageable);
}
