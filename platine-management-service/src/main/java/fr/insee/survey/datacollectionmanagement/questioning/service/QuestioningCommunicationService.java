package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface QuestioningCommunicationService {

    List<QuestioningCommunicationDto> findQuestioningCommunicationsByQuestioningId(Long questioningId);
}
