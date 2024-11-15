package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Optional;

@Service
public interface QuestioningCommunicationService {

    Optional<QuestioningCommunication> getLastQuestioningCommunication(Questioning questioning);

    QuestioningCommunicationDto convertToDto(QuestioningCommunication questioningCommunication) ;

    QuestioningCommunication convertToEntity(QuestioningCommunicationDto questioningCommunicationDto) throws ParseException;

}
