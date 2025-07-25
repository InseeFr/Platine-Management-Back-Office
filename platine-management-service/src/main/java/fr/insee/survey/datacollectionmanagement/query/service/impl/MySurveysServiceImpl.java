package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.query.QuestioningUrls;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDto;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.query.service.MySurveysService;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningUrlContext;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.component.QuestioningUrlComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MySurveysServiceImpl implements MySurveysService {

    private final QuestioningService questioningService;

    private final QuestioningUrlComponent questioningUrlComponent;

    private final QuestioningAccreditationRepository questioningAccreditationRepository;

    @Override
    public List<MyQuestionnaireDto> getListMyQuestionnaires(String contactId) {
        List<MyQuestionnaireDto> myQuestionnaireDtos = new ArrayList<>();
        List<MyQuestionnaireDetailsDto> myQuestionnaireDetailsDtos = questioningAccreditationRepository.findQuestionnaireDetailsByIdec(contactId);

        for (MyQuestionnaireDetailsDto details : myQuestionnaireDetailsDtos) {
            QuestioningUrlContext ctx = new QuestioningUrlContext(
                    details.getSurveyUnitId(),
                    details.getQuestioningId(),
                    String.format("%s-%s-%s",details.getSourceId().toLowerCase(),details.getSurveyYear(),details.getPeriod()),
                    DataCollectionEnum.valueOf(details.getDataCollectionTarget()),
                    details.getSourceId().toLowerCase(),
                    details.getSurveyYear(),
                    details.getPeriod(),
                    details.getOperationUploadReference(),
                    contactId
            );

            Date openingDate = details.getPartitioningOpeningDate();
            Date closingDate = details.getPartitioningClosingDate();

            QuestionnaireStatusTypeEnum status = isFileUpload(ctx.dataCollection())
                    ? questioningService.getQuestioningStatusFileUpload(openingDate, closingDate)
                    : questioningService.getQuestioningStatus(details.getQuestioningId(), openingDate, closingDate);

            QuestioningUrls urls = buildQuestioningUrls(status, ctx);

            MyQuestionnaireDto myQuestionnaireDto = new MyQuestionnaireDto(
                    details.getSourceId(),
                    details.getSurveyUnitIdentificationCode(),
                    details.getSurveyUnitIdentificationName(),
                    status.name(),
                    urls.accessUrl().orElse(null),
                    urls.depositProofUrl().orElse(null),
                    details.getQuestioningId(),
                    details.getPartitioningLabel(),
                    details.getPartitioningId(),
                    details.getPartitioningReturnDate() != null ? details.getPartitioningReturnDate().toInstant() : null,
                    details.getSurveyUnitId(),
                    urls.downloadUrl().orElse(null),
                    details.getOperationUploadReference()
            );

            myQuestionnaireDtos.add(myQuestionnaireDto);
        }
        return myQuestionnaireDtos;
    }

    private QuestioningUrls buildQuestioningUrls(QuestionnaireStatusTypeEnum status, QuestioningUrlContext ctx) {
        if (isFileUpload(ctx.dataCollection())) {
            return QuestioningUrls.forDownload(questioningUrlComponent.buildDownloadUrl(ctx));
        }

        if (QuestionnaireStatusTypeEnum.RECEIVED.equals(status)) {
            if (isXForm(ctx.dataCollection())) {
                return QuestioningUrls.forAccess(questioningUrlComponent.buildAccessUrl(UserRoles.INTERVIEWER, ctx));
            }
            return QuestioningUrls.forDepositProof(questioningUrlComponent.buildDepositProofUrl(ctx.questioningId(), ctx.dataCollection()));
        }
        if (isOpen(status)) {
            return QuestioningUrls.forAccess(questioningUrlComponent.buildAccessUrl(UserRoles.INTERVIEWER, ctx));
        }
        return QuestioningUrls.empty();
    }

    private boolean isXForm(DataCollectionEnum dataCollection) {
        return DataCollectionEnum.XFORM1.equals(dataCollection) || DataCollectionEnum.XFORM2.equals(dataCollection);
    }

    private boolean isFileUpload(DataCollectionEnum dataCollection) {
        return DataCollectionEnum.FILE_UPLOAD.equals(dataCollection);
    }

    private boolean isOpen(QuestionnaireStatusTypeEnum questioningStatus) {
        return QuestionnaireStatusTypeEnum.IN_PROGRESS.equals(questioningStatus) || QuestionnaireStatusTypeEnum.NOT_STARTED.equals(questioningStatus);
    }
}
