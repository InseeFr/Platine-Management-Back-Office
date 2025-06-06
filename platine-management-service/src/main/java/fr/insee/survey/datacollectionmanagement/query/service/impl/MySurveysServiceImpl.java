package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestioningDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDto;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.query.service.MySurveysService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.component.QuestioningUrlComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MySurveysServiceImpl implements MySurveysService {

    private final QuestioningAccreditationService questioningAccreditationService;

    private final PartitioningService partitioningService;

    private final QuestioningEventService questioningEventService;

    private final QuestioningService questioningService;

    private final QuestioningUrlComponent questioningUrlComponent;

    private final QuestioningAccreditationRepository questioningAccreditationRepository;

    private final String questionnaireApiUrl;

    private final String questionnaireApiSensitiveUrl;



    @Override
    public List<MyQuestioningDto> getListMySurveys(String id) {
        List<MyQuestioningDto> listSurveys = new ArrayList<>();
        List<QuestioningAccreditation> accreditations = questioningAccreditationService.findByContactIdentifier(id);

        for (QuestioningAccreditation questioningAccreditation : accreditations) {
            MyQuestioningDto surveyDto = new MyQuestioningDto();
            Questioning questioning = questioningAccreditation.getQuestioning();
            Partitioning part = partitioningService.findById(questioning.getIdPartitioning());
            Survey survey = part.getCampaign().getSurvey();
            String surveyUnitId = questioning.getSurveyUnit().getIdSu();
            surveyDto.setSurveyWording(survey.getLongWording());
            surveyDto.setSurveyObjectives(survey.getLongObjectives());
            surveyDto.setAccessUrl(
                    questioningUrlComponent.getAccessUrl(UserRoles.INTERVIEWER, questioning, part));
            surveyDto.setIdentificationCode(surveyUnitId);
            surveyDto.setOpeningDate(new Timestamp(part.getOpeningDate().getTime()));
            surveyDto.setClosingDate(new Timestamp(part.getClosingDate().getTime()));
            surveyDto.setReturnDate(new Timestamp(part.getReturnDate().getTime()));
            surveyDto.setMandatoryMySurveys(part.getCampaign().getSurvey().getSource().getMandatoryMySurveys());

            Optional<QuestioningEvent> questioningEvent = questioningEventService.getLastQuestioningEvent(
                    questioning, TypeQuestioningEvent.MY_QUESTIONINGS_EVENTS);
            if (questioningEvent.isPresent()) {
                surveyDto.setQuestioningStatus(questioningEvent.get().getType().name());
                surveyDto.setQuestioningDate(new Timestamp(questioningEvent.get().getDate().getTime()));
            } else {
                log.debug("No questioningEvents found for questioning {} for identifier {}",
                        questioning.getId(), id);


            }
            listSurveys.add(surveyDto);

        }
        log.info("Get my questionings for id {} - nb results: {}", id, listSurveys.size());
        return listSurveys;
    }

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
