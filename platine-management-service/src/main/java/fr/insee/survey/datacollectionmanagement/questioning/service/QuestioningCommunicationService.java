package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationInputDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface QuestioningCommunicationService {

    List<QuestioningCommunicationDto> findQuestioningCommunicationsByQuestioningId(UUID questioningId);

    void postQuestioningCommunication(QuestioningCommunicationInputDto questioningCommunicationInputDto);

    void deleteQuestioningCommunication(Long questioningCommunicationId);


}
