package fr.insee.survey.datacollectionmanagement.questioning.service.impl;


import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningComment;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommentInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommentOutputDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningCommentRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class QuestioningCommentServiceImpl implements QuestioningCommentService {

    private final QuestioningCommentRepository questioningCommentRepository;
    private final ModelMapper modelMapper;

    @Override
    public QuestioningCommentOutputDto saveQuestioningComment(Questioning questioning, QuestioningCommentInputDto questioningCommentInputDto) {
        QuestioningComment questioningComment = convertToEntity(questioningCommentInputDto);
        questioningComment.setDate(new Date());
        questioningComment.setQuestioning(questioning);
        QuestioningComment newQuestioningComment = questioningCommentRepository.save(questioningComment);
        return convertToOutputDto(newQuestioningComment);
    }

    public QuestioningComment convertToEntity(QuestioningCommentInputDto questioningCommentDto) {
        return modelMapper.map(questioningCommentDto, QuestioningComment.class);
    }

    @Override
    public QuestioningCommentOutputDto convertToOutputDto(QuestioningComment questioningComment) {
        return modelMapper.map(questioningComment, QuestioningCommentOutputDto.class);
    }

}
