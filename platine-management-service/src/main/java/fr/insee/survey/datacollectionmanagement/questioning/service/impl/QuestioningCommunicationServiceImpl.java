package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningCommunicationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class QuestioningCommunicationServiceImpl implements QuestioningCommunicationService {
    private final ModelMapper modelMapper;

    private final QuestioningRepository questioningRepository;

    @Override
    public List<QuestioningCommunicationDto> findQuestioningCommunicationsByQuestioningId(UUID questioningId) {
        Optional<Questioning> questioning = questioningRepository.findById(questioningId);
        if (questioning.isEmpty()) {
            return List.of();
        }
        Set<QuestioningCommunication> questioningCommunications = questioning.get().getQuestioningCommunications();
        return questioningCommunications.stream()
                .map(questioningCommunication -> modelMapper.map(questioningCommunication, QuestioningCommunicationDto.class))
                .toList();
    }

}
