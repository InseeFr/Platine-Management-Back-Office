package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningComment;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommentInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommentOutputDto;

public interface QuestioningCommentService {

    QuestioningCommentOutputDto saveQuestioningComment(Questioning questioning, QuestioningCommentInputDto questioningCommentInputDto);

    QuestioningCommentOutputDto convertToOutputDto(QuestioningComment questioningComment);
}
