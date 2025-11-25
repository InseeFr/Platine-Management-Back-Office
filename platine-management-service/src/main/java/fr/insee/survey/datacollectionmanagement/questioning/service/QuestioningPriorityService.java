package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.InterrogationPriorityInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.validation.ValidationQuestioningPriorityError;

import java.util.List;

public interface QuestioningPriorityService {

    List<ValidationQuestioningPriorityError> validatePriorityRules(List<InterrogationPriorityInputDto> priorities);

}
