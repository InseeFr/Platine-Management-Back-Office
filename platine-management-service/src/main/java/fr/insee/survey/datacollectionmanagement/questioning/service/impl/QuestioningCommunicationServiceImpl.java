package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeCommunicationEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningCommunicationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningCommunicationService;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Transactional
public class QuestioningCommunicationServiceImpl implements QuestioningCommunicationService {
    private final ModelMapper modelMapper;

    private final QuestioningRepository questioningRepository;

    private final QuestioningCommunicationRepository questioningCommunicationRepository;

  @Override
  public List<QuestioningCommunicationDto> findQuestioningCommunicationsByQuestioningId(UUID questioningId) {
    List<QuestioningCommunication> communications = questioningCommunicationRepository.findByQuestioningId(questioningId);

    return communications.stream()
        .map(communication -> modelMapper.map(communication, QuestioningCommunicationDto.class))
        .toList();
  }

  @Override
  public boolean postQuestioningCommunication(String communicationType,
      QuestioningCommunicationInputDto questioningCommunicationInputDto) {
    UUID questioningId = questioningCommunicationInputDto.questioningId();
    Questioning questioning = questioningRepository.findById(questioningId)
        .orElseThrow(() -> new NotFoundException(String.format("Questioning %s does not exist", questioningId)));

    QuestioningCommunication newQuestioningCommunication = new QuestioningCommunication();
    newQuestioningCommunication.setQuestioning(questioning);
    newQuestioningCommunication.setType(TypeCommunicationEvent.valueOf(communicationType));
    newQuestioningCommunication.setStatus(questioningCommunicationInputDto.status());
    newQuestioningCommunication.setDate(questioningCommunicationInputDto.date());
    newQuestioningCommunication = questioningCommunicationRepository.save(newQuestioningCommunication);

    // Update the bidirectional link
    questioning.getQuestioningCommunications().add(newQuestioningCommunication);
    return true;
  }

}
