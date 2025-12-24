package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.RessourceNotValidatedException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.query.domain.ResultUpload;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogUploadQuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Upload;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationInputDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.UploadDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.StatusCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeCommunicationEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.UploadRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningCommunicationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final UploadRepository uploadRepository;

    private final QuestioningEventService questioningEventService;

    private final QuestioningCommunicationService questioningCommunicationService;

    private final CampaignService campaignService;

    private final QuestioningService questioningService;


    @Override
    public ResultUpload save(String idCampaign, UploadDto uploadDto) throws RessourceNotValidatedException {

        ResultUpload result = new ResultUpload();
        Date today = new Date();

        // Check campaign exists and date in intervals
        if (!checkUploadDate(idCampaign, today))
            throw new RessourceNotValidatedException("Campaign", idCampaign);

        // Creating and saving the upload to get the id
        Upload up = new Upload(null, today.getTime(), null);
        up = saveAndFlush(up);
        // Creation of managementMonitoringInfo list and saving of link with upload
        List<QuestioningEvent> liste = new ArrayList<>();

        for (MoogUploadQuestioningEventDto mmDto : uploadDto.getData()) {
            String identifier = (mmDto.getIdSu() != null) ? mmDto.getIdSu() : mmDto.getIdContact();
            try {
                QuestioningEvent qe = new QuestioningEvent();

                Campaign campaign = campaignService.findById(idCampaign);
                Set<Partitioning> setParts = campaign.getPartitionings();
                if (setParts.isEmpty()) {
                    throw new RessourceNotValidatedException("No partitionings found for campaign ", idCampaign);
                }

                Set<Questioning> questionings = questioningService.findBySurveyUnitIdSu(mmDto.getIdSu());

                List<String> listIdParts = campaignService.findById(idCampaign).getPartitionings().stream().map(Partitioning::getId).toList();
                Optional<Questioning> quest = questionings.stream().filter(q -> listIdParts.contains(q.getIdPartitioning())).findFirst();

                qe.setUpload(up);
                //Service used by moog. Useful if you don't want to change moog and want to have both VALPAP and RECUPAP status in the csv sent.
                if(mmDto.getStatus().equals("VALPAP")){
                    mmDto.setStatus("RECUPAP");
                }
                qe.setType(TypeQuestioningEvent.valueOf(mmDto.getStatus()));
                qe.setQuestioning(quest.get());
                ObjectNode payload = JsonNodeFactory.instance.objectNode();

                payload.put("source", "Moog IHM - Post Event by upload");
                payload.put("author", mmDto.getIdContact());
                qe.setPayload(payload);
                qe.setDate(today);
                liste.add(questioningEventService.saveQuestioningEvent(qe));

                if(TypeQuestioningEvent.valueOf(mmDto.getStatus()).equals(TypeQuestioningEvent.PND))
                {
                  questioningCommunicationService.postQuestioningCommunication(new QuestioningCommunicationInputDto(quest.get().getId(),
                      StatusCommunication.MANUAL, TypeCommunicationEvent.PND));

                }

                if (quest.isPresent()) {
                    quest.get().getQuestioningEvents().add(qe);
                    questioningService.saveQuestioning(quest.get());
                }
                result.addIdOk(identifier);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                log.info("Info: id KO {}", e.getMessage());
                result.addIdKo(identifier, "RessourceNotFound or unprocessable request");
            }
        }
        if (result.getListIdOK().isEmpty()) {
            delete(up);
            return result;
        }
        up.setQuestioningEvents(liste);
        saveAndFlush(up);

        return result;
    }

    @Override
    public Upload findById(long id) {
        return uploadRepository.findById(id).orElseThrow(()-> new NotFoundException(String.format("Upload %s not found", id)));
    }

    @Override
    public List<Upload> findAllByIdCampaign(String idCampaign) {

        Campaign campaign = campaignService.findById(idCampaign);

        List<String> partitioningIds = campaign.getPartitionings().stream().map(Partitioning::getId).toList();

        // Keeps the uploads which first managementMonitoringInfo belongs to the survey
        return uploadRepository.findAll().stream().filter(upload -> !upload.getQuestioningEvents().isEmpty())
                .filter(upload -> partitioningIds.contains(upload.getQuestioningEvents().stream().findFirst().get().getQuestioning().getIdPartitioning()
                ))
                .toList();

    }


    @Override
    public void delete(Upload up) {
        uploadRepository.delete(up);
    }

    @Override
    public Upload saveAndFlush(Upload up) {
        return uploadRepository.saveAndFlush(up);
    }

    @Override
    public boolean checkUploadDate(String idCampaign, Date date) {
        Campaign campaign = campaignService.findById(idCampaign);
        long timestamp = date.getTime();
        Optional<Date> openingDate = campaign.getPartitionings().stream().map(Partitioning::getOpeningDate)
                .toList().stream()
                .min(Comparator.comparing(Date::getTime));
        Optional<Date> closingDate = campaign.getPartitionings().stream().map(Partitioning::getClosingDate)
                .toList().stream()
                .max(Comparator.comparing(Date::getTime));
        if (openingDate.isPresent() && closingDate.isPresent()) {
            long start = openingDate.get().getTime();
            long end = closingDate.get().getTime();
            return (start < timestamp && timestamp < end);


        }
        return false;
    }

    @Override
    public void removeEmptyUploads() {
        uploadRepository.findByQuestioningEventsIsEmpty().forEach(uploadRepository::delete);
    }
}
