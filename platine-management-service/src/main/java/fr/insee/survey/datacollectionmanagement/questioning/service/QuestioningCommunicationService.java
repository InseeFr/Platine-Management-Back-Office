package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationInputDto;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public interface QuestioningCommunicationService {

    List<QuestioningCommunicationDto> findQuestioningCommunicationsByQuestioningId(UUID questioningId);

    boolean postQuestioningCommunication(String communicationEventType, QuestioningCommunicationInputDto questioningCommunicationInputDto);

}
