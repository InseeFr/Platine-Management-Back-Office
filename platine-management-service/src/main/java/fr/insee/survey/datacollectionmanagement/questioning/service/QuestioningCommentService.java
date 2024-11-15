package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningComment;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommentInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommentOutputDto;

public interface QuestioningCommentService {

    QuestioningComment saveQuestioningComment(QuestioningComment questioningComment);

    QuestioningComment convertToEntity(QuestioningCommentInputDto questioningCommentDto);

    QuestioningCommentOutputDto convertToOutputDto(QuestioningComment questioningComment);
}
