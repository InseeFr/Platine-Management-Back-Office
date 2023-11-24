package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.survey.datacollectionmanagement.exception.RessourceNotValidatedException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.query.domain.ResultUpload;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogUploadQuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Upload;
import fr.insee.survey.datacollectionmanagement.questioning.dto.UploadDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.UploadRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.UploadService;
import fr.insee.survey.datacollectionmanagement.questioning.util.TypeQuestioningEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final UploadRepository uploadRepository;

    private final QuestioningEventService questioningEventService;

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

                Optional<Campaign> campaign = campaignService.findById(idCampaign);
                if (campaign.isEmpty()) {
                    throw new RessourceNotValidatedException("Campaign", idCampaign);
                }
                Set<Partitioning> setParts = campaign.get().getPartitionings();
                if (setParts.isEmpty()) {
                    throw new RessourceNotValidatedException("No partitionings found for campaign ", idCampaign);
                }

                Set<Questioning> questionings = questioningService.findBySurveyUnitIdSu(mmDto.getIdSu());

                List<String> listIdParts = campaignService.findById(idCampaign).get().getPartitionings().stream().map(Partitioning::getId).toList();
                Optional<Questioning> quest = questionings.stream().filter(q -> listIdParts.contains(q.getIdPartitioning()) && q.getQuestioningAccreditations().stream().map(QuestioningAccreditation::getIdContact)
                        .toList().contains(mmDto.getIdContact())).findFirst();

                qe.setUpload(up);
                qe.setType(TypeQuestioningEvent.valueOf(mmDto.getStatus()));
                qe.setQuestioning(quest.get());
                JSONObject jo = new JSONObject();
                jo.put("source", "Moog IHM - Post Event by upload");
                ObjectMapper objectMapper = new ObjectMapper();
                qe.setPayload(objectMapper.readTree(jo.toString()));
                qe.setDate(today);
                liste.add(questioningEventService.saveQuestioningEvent(qe));
                if (quest.isPresent()) {
                    quest.get().getQuestioningEvents().add(qe);
                    questioningService.saveQuestioning(quest.get());
                }
                result.addIdOk(identifier);
            } catch (Exception e) {
                log.error("Error in request");
                log.info("Info: id KO " + e.getMessage());
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
    public Optional<Upload> findById(long id) {
        return uploadRepository.findById(id);
    }

    @Override
    public List<Upload> findAllByIdCampaign(String idCampaign) {

        Optional<Campaign> campaign = campaignService.findById(idCampaign);
        if (campaign.isPresent()) {

            List<String> partitioningIds = campaign.get().getPartitionings().stream().map(Partitioning::getId).toList();

            // Keeps the uploads which first managementMonitoringInfo belongs to the survey
            return uploadRepository.findAll().stream().filter(upload -> !upload.getQuestioningEvents().isEmpty())
                    .filter(upload -> partitioningIds.contains(upload.getQuestioningEvents().stream().findFirst().get().getQuestioning().getIdPartitioning()
                    ))
                    .toList();
        }
        return Collections.emptyList();
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
        Optional<Campaign> campaign = campaignService.findById(idCampaign);
        long timestamp = date.getTime();
        if (campaign.isPresent()) {
            Optional<Date> openingDate = campaign.get().getPartitionings().stream().map(Partitioning::getOpeningDate)
                    .toList().stream()
                    .min(Comparator.comparing(Date::getTime));
            Optional<Date> closingDate = campaign.get().getPartitionings().stream().map(Partitioning::getClosingDate)
                    .toList().stream()
                    .max(Comparator.comparing(Date::getTime));
            if(openingDate.isPresent() && closingDate.isPresent()){
                long start = openingDate.get().getTime();
                long end = closingDate.get().getTime();
                return (start < timestamp && timestamp < end);

            }

        }
        return false;
    }

    @Override
    public void removeEmptyUploads() {
        uploadRepository.findByQuestioningEventsIsEmpty().forEach(uploadRepository::delete);
    }
}
