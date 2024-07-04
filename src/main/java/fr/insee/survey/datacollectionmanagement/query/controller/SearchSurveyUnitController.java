package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchSurveyUnitContactDto;
import fr.insee.survey.datacollectionmanagement.query.dto.SurveyUnitPartitioningDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.util.TypeQuestioningEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@PreAuthorize("hasRole('INTERNAL_USER') "
        + "|| hasRole('WEB_CLIENT') "
        + "|| hasRole('ADMIN') ")
@Tag(name = "4 - Cross domain")
@Slf4j
@RequiredArgsConstructor
public class SearchSurveyUnitController {


    private final ContactService contactService;

    private final QuestioningService questioningService;

    private final QuestioningAccreditationService questioningAccreditationService;

    private final PartitioningService partitioningService;

    private final QuestioningEventService questioningEventService;


    @GetMapping(path = Constants.API_SURVEY_UNITS_CONTACTS, produces = "application/json")
    @Operation(summary = "Get contacts authorised to respond to a survey for a survey unit")
    public ResponseEntity<List<SearchSurveyUnitContactDto>> getSurveyUnitContacts(
            @PathVariable("id") String id) {

        List<String> listContactIdentifiers = new ArrayList<>();
        Set<Questioning> setQuestionings = questioningService.findBySurveyUnitIdSu(id);
        for (Questioning questioning : setQuestionings) {
            for (QuestioningAccreditation qa : questioning.getQuestioningAccreditations()) {
                if (!listContactIdentifiers.contains(qa.getIdContact()))
                    listContactIdentifiers.add(qa.getIdContact());

            }
        }

        List<SearchSurveyUnitContactDto> listResult = new ArrayList<>();
        for (String identifier : listContactIdentifiers) {
            SearchSurveyUnitContactDto searchSurveyUnitContactDto = new SearchSurveyUnitContactDto();
            Contact contact = contactService.findByIdentifier(identifier);
            searchSurveyUnitContactDto.setIdentifier(identifier);
            searchSurveyUnitContactDto.setCity(contact.getEmail());
            searchSurveyUnitContactDto.setEmail(contact.getEmail());
            searchSurveyUnitContactDto.setFirstName(contact.getFirstName());
            searchSurveyUnitContactDto.setLastName(contact.getLastName());
            searchSurveyUnitContactDto.setPhoneNumber(contact.getPhone());
            searchSurveyUnitContactDto.setCity(contact.getAddress() != null ? contact.getAddress().getCityName() : null);
            searchSurveyUnitContactDto.setListSourcesId(questioningAccreditationService.findByContactIdentifier(identifier).stream().
                    filter(qa -> qa.getQuestioning().getSurveyUnit().getIdSu().equalsIgnoreCase(id)).
                    map(qa -> partitioningService.findById(qa.getQuestioning().getIdPartitioning()).getCampaign().getSurvey().getSource().getId()).
                    distinct().toList());
            listResult.add(searchSurveyUnitContactDto);
        }

        return new ResponseEntity<>(listResult, HttpStatus.OK);

    }


    @GetMapping(path = Constants.API_SURVEY_UNITS_PARTITIONINGS, produces = "application/json")
    @Operation(summary = "Get contacts authorised to respond to a survey for a survey unit")
    public ResponseEntity<List<SurveyUnitPartitioningDto>> getSurveyUnitPartitionings(
            @PathVariable("id") String id,
            @RequestParam(defaultValue = "false") boolean isFilterOpened) {

        List<SurveyUnitPartitioningDto> listParts = new ArrayList<>();
        Set<Questioning> setQuestionings = questioningService.findBySurveyUnitIdSu(id);
        for (Questioning questioning : setQuestionings) {
            Partitioning part = partitioningService.findById(questioning.getIdPartitioning());
            Optional<QuestioningEvent> questioningEvent = questioningEventService.getLastQuestioningEvent(questioning, TypeQuestioningEvent.STATE_EVENTS);

            if (!isFilterOpened || partitioningService.isOnGoing(part, new Date())) {
                Survey survey = part.getCampaign().getSurvey();
                listParts.add(new SurveyUnitPartitioningDto(
                        survey.getSource().getShortWording(),
                        survey.getYear(),
                        part.getCampaign().getPeriod(),
                        part.getCampaign().getCampaignWording(),
                        part.getClosingDate(),
                        questioningEvent.map(QuestioningEvent::getType).orElse(null)
                ));
            }

        }

        return new ResponseEntity<>(listParts, HttpStatus.OK);

    }

}
