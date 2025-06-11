package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDto;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.query.service.MySurveysService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.component.QuestioningUrlComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class MySurveysServiceImpl implements MySurveysService {

    private final PartitioningService partitioningService;

    private final QuestioningService questioningService;

    private final QuestioningUrlComponent questioningUrlComponent;

    private final QuestioningAccreditationRepository questioningAccreditationRepository;

    private final String questionnaireApiUrl;

    private final String questionnaireApiSensitiveUrl;

    public boolean isXForm(DataCollectionEnum dataCollection) {
        return DataCollectionEnum.XFORM1.equals(dataCollection) || DataCollectionEnum.XFORM2.equals(dataCollection);
    }

    public boolean isOpen(QuestionnaireStatusTypeEnum questioningStatus) {
        return QuestionnaireStatusTypeEnum.IN_PROGRESS.equals(questioningStatus) || QuestionnaireStatusTypeEnum.NOT_STARTED.equals(questioningStatus);
    }


    public void setQuestioningAccessUrl(MyQuestionnaireDto questionnaireDto,
                                         Questioning questioning,
                                         Partitioning partitioning,
                                         String id) {

        String url = questioningUrlComponent.getAccessUrlWithContactId(
                UserRoles.INTERVIEWER,
                questioning,
                partitioning,
                id
        );
        questionnaireDto.setQuestioningAccessUrl(url);
    }

    public String buildDepositProofUrl(MyQuestionnaireDetailsDto detailsDto, DataCollectionEnum dataCollection) {
        String path = "/api/survey-unit/" + detailsDto.getSurveyUnitId() + "/deposit-proof";

        if (DataCollectionEnum.LUNATIC_NORMAL.equals(dataCollection)) {
            return questionnaireApiUrl + path;
        }

        if (DataCollectionEnum.LUNATIC_SENSITIVE.equals(dataCollection)) {
            return questionnaireApiSensitiveUrl + path;
        }

        return null;
    }

    public void handleStatus(QuestionnaireStatusTypeEnum questioningStatus,
                                 MyQuestionnaireDetailsDto myQuestionnaireDetailsDto,
                                 MyQuestionnaireDto myQuestionnaireDto,
                                 Questioning questioning,
                                 Partitioning partitioning,
                                 String id) {

        if (QuestionnaireStatusTypeEnum.RECEIVED.equals(questioningStatus)) {
            DataCollectionEnum dataCollectionEnum = DataCollectionEnum.valueOf(myQuestionnaireDetailsDto.getDataCollectionTarget());
            if (isXForm(dataCollectionEnum)) {
                setQuestioningAccessUrl(myQuestionnaireDto, questioning, partitioning, id);
                return;
            }
            String depositProofUrl = buildDepositProofUrl(myQuestionnaireDetailsDto, dataCollectionEnum);
            myQuestionnaireDto.setDepositProofUrl(depositProofUrl);
            return;

        }

        if (isOpen(questioningStatus)) {
            setQuestioningAccessUrl(myQuestionnaireDto, questioning, partitioning, id);
        }
    }


    @Override
    public List<MyQuestionnaireDto> getListMyQuestionnaires(String id) {
        List<MyQuestionnaireDto> myQuestionnaireDtos = new ArrayList<>();
        List<MyQuestionnaireDetailsDto> myQuestionnaireDetailsDtos = questioningAccreditationRepository.findQuestionnaireDetailsByIdec(id);

        for (MyQuestionnaireDetailsDto myQuestionnaireDetailsDto : myQuestionnaireDetailsDtos) {
            MyQuestionnaireDto myQuestionnaireDto = new MyQuestionnaireDto();
            myQuestionnaireDtos.add(myQuestionnaireDto);

            myQuestionnaireDto.setSourceId(myQuestionnaireDetailsDto.getSourceId());
            myQuestionnaireDto.setQuestioningId(myQuestionnaireDetailsDto.getQuestioningId());
            myQuestionnaireDto.setPartitioningLabel(myQuestionnaireDetailsDto.getPartitioningLabel());
            myQuestionnaireDto.setSurveyUnitIdentificationCode(myQuestionnaireDetailsDto.getSurveyUnitIdentificationCode());
            myQuestionnaireDto.setSurveyUnitIdentificationName(myQuestionnaireDetailsDto.getSurveyUnitIdentificationName());
            myQuestionnaireDto.setSurveyUnitId(myQuestionnaireDetailsDto.getSurveyUnitId());
            myQuestionnaireDto.setPartitioningId(myQuestionnaireDetailsDto.getPartitioningId());
            myQuestionnaireDto.setPartitioningReturnDate(myQuestionnaireDetailsDto.getPartitioningReturnDate().toInstant());

            Questioning questioning = questioningService.findById(myQuestionnaireDetailsDto.getQuestioningId());
            Partitioning partitioning = partitioningService.findById(myQuestionnaireDetailsDto.getPartitioningId());
            QuestionnaireStatusTypeEnum questioningStatus = questioningService.getQuestioningStatus(questioning, partitioning);
            myQuestionnaireDto.setQuestioningStatus(questioningStatus.name());

            handleStatus(questioningStatus, myQuestionnaireDetailsDto, myQuestionnaireDto, questioning, partitioning, id);
        }
        return myQuestionnaireDtos;
    }
}
