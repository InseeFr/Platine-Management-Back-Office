package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnitEvent;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitEventRequestDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitEventResponseDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitEventRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveyUnitEventServiceImpl implements SurveyUnitEventService {
    private final SurveyUnitEventRepository surveyUnitEventRepository;
    private final SurveyUnitService surveyUnitService;
    private final CampaignService campaignService;
    private final Clock clock;

    @Override
    public List<SurveyUnitEventResponseDto> getEvents(String surveyUnitId) {
        return surveyUnitEventRepository
                .findBySurveyUnitIdSuOrderByDateDesc(surveyUnitId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public void createEvent(SurveyUnitEventRequestDto eventDto, String surveyUnitId) {
        List<String> campaignIds = surveyUnitService.getCampaignIds(surveyUnitId);
        if(! campaignIds.contains(eventDto.campaignId())) {
            throw new NotFoundException("Campaign not found in survey unit's campaigns");
        }

        SurveyUnit surveyUnit = surveyUnitService.findbyId(surveyUnitId);
        Campaign campaign = campaignService.findById(eventDto.campaignId());

        Instant eventDate = Instant.ofEpochMilli(eventDto.eventDate());

        SurveyUnitEvent event = new SurveyUnitEvent(
                surveyUnit,
                campaign,
                LocalDateTime.ofInstant(eventDate, clock.getZone()),
                LocalDateTime.now(clock),
                eventDto.eventType(),
                eventDto.source());

        surveyUnitEventRepository.save(event);
    }

    private SurveyUnitEventResponseDto toDto(SurveyUnitEvent event) {
        return new SurveyUnitEventResponseDto(
                event.getCampaign().getId(),
                event.getType(),
                event.getSource(),
                event.getDate()
                        .atZone(clock.getZone()).toInstant().toEpochMilli(),
                event.getCreationDate()
                        .atZone(clock.getZone()).toInstant().toEpochMilli());
    }
}
