package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.query.dto.AssistanceDto;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDto;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningIdDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.Set;

@Setter
public class QuestioningServiceStub implements QuestioningService {

    private QuestionnaireStatusTypeEnum questionnaireStatus;

    @Override
    public Page<Questioning> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Questioning findById(Long id) {
        return null;
    }

    @Override
    public Questioning saveQuestioning(Questioning questioning) {
        return null;
    }

    @Override
    public void deleteQuestioning(Long id) {
        //not used
    }

    @Override
    public Set<Questioning> findByIdPartitioning(String idPartitioning) {
        return Set.of();
    }

    @Override
    public Optional<Questioning> findByIdPartitioningAndSurveyUnitIdSu(String idPartitioning, String surveyUnitIdSu) {
        return Optional.empty();
    }

    @Override
    public QuestioningIdDto findByCampaignIdAndSurveyUnitIdSu(String campaignId, String surveyUnitIdSu) {
        return null;
    }

    @Override
    public AssistanceDto getMailAssistanceDto(Long questioningId) {
        return null;
    }

    @Override
    public int deleteQuestioningsOfOnePartitioning(Partitioning partitioning) {
        return 0;
    }

    @Override
    public Set<Questioning> findBySurveyUnitIdSu(String idSu) {
        return Set.of();
    }

    @Override
    public Page<SearchQuestioningDto> searchQuestioning(String param, Pageable pageable) {
        return null;
    }

    @Override
    public QuestioningDetailsDto getQuestioningDetails(Long id) {
        return null;
    }

    @Override
    public QuestionnaireStatusTypeEnum getQuestioningStatus(Questioning questioning, Partitioning part) {
        return questionnaireStatus;
    }
}
