package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningCommunicationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Comparator;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestioningCommunicationServiceImpl implements QuestioningCommunicationService {
    private final ModelMapper modelMapper;

    @Override
    public Optional<QuestioningCommunication> getLastQuestioningCommunication(Questioning questioning) {
        return questioning.getQuestioningCommunications().stream().min(Comparator.comparing(QuestioningCommunication::getDate));
    }

    @Override
    public QuestioningCommunicationDto convertToDto(QuestioningCommunication questioningCommunication) {
        return modelMapper.map(questioningCommunication, QuestioningCommunicationDto.class);
    }

    @Override
    public QuestioningCommunication convertToEntity(QuestioningCommunicationDto questioningCommunicationDto) throws ParseException {
        return modelMapper.map(questioningCommunicationDto, QuestioningCommunication.class);
    }

}
