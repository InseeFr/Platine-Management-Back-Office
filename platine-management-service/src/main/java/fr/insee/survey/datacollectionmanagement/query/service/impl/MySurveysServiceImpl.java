package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestioningDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDto;
import fr.insee.survey.datacollectionmanagement.query.enums.QuestionnaireStatusTypeEnum;
import fr.insee.survey.datacollectionmanagement.query.service.MySurveysService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
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
                    questioningService.getAccessUrl(UserRoles.INTERVIEWER, questioning, part));
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


    @Override
    public List<MyQuestionnaireDto> getListMyQuestionnaires(String id, String QuestionnaireApiUrl) {
        List<MyQuestionnaireDto> myQuestionnaireDtos = new ArrayList<>();
        List<QuestioningAccreditation> accreditations = questioningAccreditationService.findByContactIdentifier(id);

        for (QuestioningAccreditation questioningAccreditation : accreditations) {
            MyQuestionnaireDto myQuestionnaireDto = new MyQuestionnaireDto();
            Questioning questioning = questioningAccreditation.getQuestioning();
            Partitioning part = partitioningService.findById(questioning.getIdPartitioning());
            myQuestionnaireDto.setSourceId(part.getCampaign().getSurvey().getSource().getId());
            myQuestionnaireDto.setQuestioningId(questioning.getId());
            myQuestionnaireDto.setPartitioningLabel(part.getLabel());
            myQuestionnaireDto.setSurveyUnitIdentificationCode(questioning.getSurveyUnit().getIdentificationCode());
            myQuestionnaireDto.setSurveyUnitIdentificationName(questioning.getSurveyUnit().getIdentificationName());
            myQuestionnaireDtos.add(myQuestionnaireDto);
            myQuestionnaireDto.setSurveyUnitId(questioning.getSurveyUnit().getIdSu());
            myQuestionnaireDto.setPartitioningId(part.getId());

            String questioningStatus = questioningService.getQuestioningStatus(questioning, part).name();
            myQuestionnaireDto.setQuestioningStatus(questioningStatus);

            if(questioningStatus.equals(QuestionnaireStatusTypeEnum.RECEIVED.name()))
            {
                DataCollectionEnum dataCollectionEnum = part.getCampaign().getDataCollectionTarget();
                if(dataCollectionEnum.equals(DataCollectionEnum.XFORM1) || dataCollectionEnum.equals(DataCollectionEnum.XFORM2))
                {
                    String depositProofUrl = QuestionnaireApiUrl +  "/api/survey-unit/" + questioning.getSurveyUnit().getIdSu() + "/deposit-proof";
                    myQuestionnaireDto.setQuestioningAccessUrl(depositProofUrl);
                }
                else
                {
                    myQuestionnaireDto.setDepositProofUrl("http://preuve-de-depot/" + questioning.getSurveyUnit().getIdSu());
                }
            }

            if(questioningStatus.equals(QuestionnaireStatusTypeEnum.OPEN.name()))
                myQuestionnaireDto.setQuestioningAccessUrl(questioningService.getAccessUrl(UserRoles.INTERVIEWER, questioning, part));
        }

        return myQuestionnaireDtos;
    }
}
