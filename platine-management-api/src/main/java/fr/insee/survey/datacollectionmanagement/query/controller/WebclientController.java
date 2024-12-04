package fr.insee.survey.datacollectionmanagement.query.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.dto.ContactDto;
import fr.insee.survey.datacollectionmanagement.contact.service.AddressService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.exception.NotMatchException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.*;
import fr.insee.survey.datacollectionmanagement.metadata.dto.*;
import fr.insee.survey.datacollectionmanagement.metadata.service.*;
import fr.insee.survey.datacollectionmanagement.query.dto.ContactAccreditationDto;
import fr.insee.survey.datacollectionmanagement.query.dto.EligibleDto;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningWebclientDto;
import fr.insee.survey.datacollectionmanagement.query.dto.StateDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Slf4j
@Tag(name = "6 - Webclients", description = "Enpoints for webclients")
@RequiredArgsConstructor
@Validated
public class WebclientController {

    public static final String QUESTIONING_DOES_NOT_EXIST = "Questioning does not exist";
    private final QuestioningService questioningService;

    private final SurveyUnitService surveyUnitService;

    private final PartitioningService partitioningService;

    private final SourceService sourceService;

    private final SurveyService surveyService;

    private final CampaignService campaignService;

    private final OwnerService ownerService;

    private final SupportService supportService;

    private final ContactService contactService;

    private final AddressService addressService;

    private final ViewService viewService;

    private final QuestioningEventService questioningEventService;

    private final QuestioningAccreditationService questioningAccreditationService;

    private final ModelMapper modelMapper;

    @Operation(summary = "Create or update questioning for webclients - Returns the questioning and all its accreditations")
    @PutMapping(value = Constants.API_WEBCLIENT_QUESTIONINGS, produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = QuestioningWebclientDto.class))),
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = QuestioningWebclientDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")

    })
    @Transactional
    public ResponseEntity<QuestioningWebclientDto> putQuestioning(@RequestBody QuestioningWebclientDto questioningWebclientDto)
            throws JsonProcessingException {

        log.info("Put questioning for webclients {}", questioningWebclientDto.toString());
        String modelName = questioningWebclientDto.getModelName();
        String idSu = StringUtils.upperCase(questioningWebclientDto.getSurveyUnit().getIdSu());
        String idPartitioning = StringUtils.upperCase(questioningWebclientDto.getIdPartitioning());


        Partitioning part = partitioningService.findById(idPartitioning);


        QuestioningWebclientDto questioningReturn = new QuestioningWebclientDto();
        SurveyUnit su;

        HttpStatus httpStatus = HttpStatus.OK;
        su = convertToEntity(questioningWebclientDto.getSurveyUnit());
        surveyUnitService.saveSurveyUnitAndAddress(su);

        // Create questioning if not exists
        Questioning questioning = questioningService.findByIdPartitioningAndSurveyUnitIdSu(idPartitioning, idSu);
        if (questioning == null) {
            httpStatus = HttpStatus.CREATED;
            log.info("Create questioning for partitioning={} model={} surveyunit={} ", idPartitioning, modelName,
                    idSu);
            questioning = new Questioning();
            questioning.setIdPartitioning(idPartitioning);
            questioning.setSurveyUnit(su);
            questioning.setModelName(modelName);
            questioningService.saveQuestioning(questioning);
            QuestioningEvent questioningEvent = new QuestioningEvent();
            questioningEvent.setType(TypeQuestioningEvent.INITLA);
            questioningEvent.setDate(new Date());
            questioningEvent.setQuestioning(questioning);
            questioningEventService.saveQuestioningEvent(questioningEvent);

        }


        for (ContactAccreditationDto contactAccreditationDto : questioningWebclientDto.getContacts()) {
            createContactAndAccreditations(idSu, part, questioning, contactAccreditationDto);
        }

        questioningReturn.setIdPartitioning(idPartitioning);
        questioningReturn.setModelName(modelName);
        questioningReturn.setSurveyUnit(convertToDto(questioning.getSurveyUnit()));
        List<ContactAccreditationDto> listContactAccreditationDto = new ArrayList<>();
        questioning.getQuestioningAccreditations()
                .forEach(acc -> listContactAccreditationDto
                        .add(convertToDto(contactService.findByIdentifier(acc.getIdContact()), acc.isMain())));
        questioningReturn.setContacts(listContactAccreditationDto);


        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION, ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
        log.info("Put questioning for webclients ok");
        return ResponseEntity.status(httpStatus).headers(responseHeaders).body(questioningReturn);

    }

    private void createContactAndAccreditations(String idSu, Partitioning part, Questioning questioning, ContactAccreditationDto contactAccreditationDto) throws JsonProcessingException {
        // Create contact if not exists or update
        JsonNode node = addWebclientAuthorNode();

        Contact contact;
        try {
            contact = convertToEntity(contactAccreditationDto);
            contactService.findByIdentifier(contactAccreditationDto.getIdentifier());
            if (contactAccreditationDto.getAddress() != null)
                contact.setAddress(addressService.convertToEntity(contactAccreditationDto.getAddress()));
            contactService.updateContactAddressEvent(contact, node);
        } catch (NoSuchElementException | NotFoundException e) {
            log.info("Creating contact with the identifier {}", contactAccreditationDto.getIdentifier());
            contact = convertToEntityNewContact(contactAccreditationDto);
            if (contactAccreditationDto.getAddress() != null)
                contact.setAddress(addressService.convertToEntity(contactAccreditationDto.getAddress()));
            contactService.createContactAddressEvent(contact, node);
        }

        // Create accreditations if not exists

        Set<QuestioningAccreditation> setExistingAccreditations = (questioning
                .getQuestioningAccreditations() != null) ? questioning.getQuestioningAccreditations()
                : new HashSet<>();


        List<QuestioningAccreditation> listContactAccreditations = setExistingAccreditations.stream()
                .filter(acc -> acc.getIdContact().equals(contactAccreditationDto.getIdentifier())
                        && acc.getQuestioning().getIdPartitioning().equals(part.getId())
                        && acc.getQuestioning().getSurveyUnit().getIdSu().equals(idSu))
                .toList();

        if (listContactAccreditations.isEmpty()) {
            // Create new accreditation
            QuestioningAccreditation questioningAccreditation = new QuestioningAccreditation();
            questioningAccreditation.setIdContact(contactAccreditationDto.getIdentifier());
            questioningAccreditation.setMain(contactAccreditationDto.isMain());
            questioningAccreditation.setQuestioning(questioning);
            questioningAccreditationService.saveQuestioningAccreditation(questioningAccreditation);


            // create view
            viewService.createView(contactAccreditationDto.getIdentifier(), questioning.getSurveyUnit().getIdSu(),
                    part.getCampaign().getId());

        } else {
            // update accreditation
            QuestioningAccreditation questioningAccreditation = listContactAccreditations.getFirst();
            questioningAccreditation.setMain(contactAccreditationDto.isMain());
            questioningAccreditationService.saveQuestioningAccreditation(questioningAccreditation);

        }

    }

    @Operation(summary = "Get questioning for webclients")
    @GetMapping(value = Constants.API_WEBCLIENT_QUESTIONINGS, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = QuestioningWebclientDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
    })
    public ResponseEntity<QuestioningWebclientDto> getQuestioning(@RequestParam(required = true) String modelName,
                                                                  @RequestParam(required = true) String idPartitioning,
                                                                  @RequestParam(required = true) String idSurveyUnit) {

        QuestioningWebclientDto questioningWebclientDto = new QuestioningWebclientDto();

        HttpStatus httpStatus = HttpStatus.OK;

        Questioning questioning = questioningService.findByIdPartitioningAndSurveyUnitIdSu(idPartitioning,
                idSurveyUnit);
        if (questioning == null) {
            throw new NotFoundException(QUESTIONING_DOES_NOT_EXIST);
        }

        questioningWebclientDto.setIdPartitioning(idPartitioning);
        questioningWebclientDto.setModelName(modelName);
        questioningWebclientDto.setSurveyUnit(convertToDto(questioning.getSurveyUnit()));
        List<ContactAccreditationDto> listContactAccreditationDto = new ArrayList<>();
        questioning.getQuestioningAccreditations()
                .forEach(acc -> listContactAccreditationDto
                        .add(convertToDto(contactService.findByIdentifier(acc.getIdContact()), acc.isMain())));
        questioningWebclientDto.setContacts(listContactAccreditationDto);
        return ResponseEntity.status(httpStatus).body(questioningWebclientDto);

    }

    @Operation(summary = "Search for a partitiong and metadata by partitioning id")
    @GetMapping(value = Constants.API_WEBCLIENT_METADATA_ID, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = MetadataDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<MetadataDto> getMetadata(@PathVariable("id") String id) {

        MetadataDto metadataDto = new MetadataDto();
        Partitioning part = partitioningService.findById(StringUtils.upperCase(id));
        metadataDto.setPartitioningDto(convertToDto(part));
        metadataDto.setCampaignDto(convertToDto(part.getCampaign()));
        metadataDto.setSurveyDto(convertToDto(part.getCampaign().getSurvey()));
        metadataDto.setSourceDto(convertToDto(part.getCampaign().getSurvey().getSource()));
        metadataDto.setOwnerDto(convertToDto(part.getCampaign().getSurvey().getSource().getOwner()));
        metadataDto.setSupportDto(convertToDto(part.getCampaign().getSurvey().getSource().getSupport()));
        return ResponseEntity.ok().body(metadataDto);


    }

    @Operation(summary = "Insert or update a partitiong and metadata by partitioning id")
    @PutMapping(value = Constants.API_WEBCLIENT_METADATA_ID, produces = "application/json", consumes = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = MetadataDto.class))),
            @ApiResponse(responseCode = "201", description = "Created", content = @Content(schema = @Schema(implementation = MetadataDto.class))),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @Transactional
    public ResponseEntity<MetadataDto> putMetadata(@PathVariable("id") String id,
                                                   @RequestBody @Valid MetadataDto metadataDto) {
        if (!metadataDto.getPartitioningDto().getId().equalsIgnoreCase(id)) {
            throw new NotMatchException("id and idPartitioning don't match");
        }

        MetadataDto metadataReturn = new MetadataDto();

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set(HttpHeaders.LOCATION,
                ServletUriComponentsBuilder.fromCurrentRequest().buildAndExpand(id).toUriString());
        HttpStatus httpStatus = getHttpStatus(id);


        Owner owner = convertToEntity(metadataDto.getOwnerDto());
        Support support = convertToEntity(metadataDto.getSupportDto());
        Source source = convertToEntity(metadataDto.getSourceDto());
        Survey survey = convertToEntity(metadataDto.getSurveyDto());

        survey.setSource(source);
        Campaign campaign = convertToEntity(metadataDto.getCampaignDto());
        campaign.setSurvey(survey);
        Partitioning partitioning = convertToEntity(metadataDto.getPartitioningDto());
        partitioning.setCampaign(campaign);
        source.setOwner(owner);
        source.setSupport(support);

        campaign = campaignService.insertOrUpdateCampaign(campaign);
        survey = surveyService.insertOrUpdateSurvey(survey);
        source = sourceService.insertOrUpdateSource(source);
        owner = ownerService.insertOrUpdateOwner(owner);
        support = supportService.insertOrUpdateSupport(support);
        source = sourceService.insertOrUpdateSource(source);

        metadataReturn.setOwnerDto(convertToDto(owner));
        metadataReturn.setSupportDto(convertToDto(support));
        metadataReturn.setSourceDto(convertToDto(source));
        metadataReturn.setSurveyDto(convertToDto(survey));
        metadataReturn.setCampaignDto(convertToDto(campaign));
        metadataReturn.setPartitioningDto(convertToDto(partitioning));

        return ResponseEntity.status(httpStatus).headers(responseHeaders).body(metadataReturn);


    }

    private HttpStatus getHttpStatus(String id) {
        HttpStatus httpStatus;
        try {
            partitioningService.findById(id);
            log.info("Update partitioning with the id {}", id);
            httpStatus = HttpStatus.OK;

        } catch (NotFoundException e) {
            log.info("Create partitioning with the id {}", id);
            httpStatus = HttpStatus.CREATED;
        }
        return httpStatus;
    }

    @Operation(summary = "Search for main contact")
    @GetMapping(value = Constants.API_MAIN_CONTACT, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<ContactDto> getMainContact(
            @RequestParam(value = "partitioning", required = true) String partitioningId,
            @RequestParam(value = "survey-unit", required = true) String surveyUnitId) {

        try {

            Questioning questioning = questioningService
                    .findByIdPartitioningAndSurveyUnitIdSu(partitioningId,
                            surveyUnitId);
            if (questioning != null) {
                List<QuestioningAccreditation> listQa = questioning.getQuestioningAccreditations().stream()
                        .filter(qa -> qa.isMain()).toList();
                if (listQa != null && !listQa.isEmpty()) {
                    Contact c = contactService.findByIdentifier(listQa.get(0).getIdContact());
                    return ResponseEntity.status(HttpStatus.OK).body(convertToDto((c)));


                }
            }
            throw new NotFoundException("Contact does not exist");

        } catch (NoSuchElementException e) {
            throw new NotFoundException(QUESTIONING_DOES_NOT_EXIST);
        }
    }

    @Operation(summary = "Get state of the last questioningEvent")
    @GetMapping(value = Constants.API_WEBCLIENT_STATE, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = StateDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<StateDto> getState(@PathVariable("idPartitioning") String idPartitioning,
                                             @PathVariable("idSu") String idSu) {

        Questioning questioning = questioningService.findByIdPartitioningAndSurveyUnitIdSu(
                idPartitioning, idSu);
        if (questioning == null) {
            throw new NotFoundException(QUESTIONING_DOES_NOT_EXIST);
        }
        Optional<QuestioningEvent> questioningEvent = questioningEventService.getLastQuestioningEvent(questioning,
                TypeQuestioningEvent.STATE_EVENTS);
        StateDto result = new StateDto();
        result.setState(questioningEvent.isPresent() ? questioningEvent.get().getType().name() : "null");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "Indicates whether a questioning should be follow up or not")
    @GetMapping(value = Constants.API_WEBCLIENT_FOLLOWUP, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EligibleDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<EligibleDto> isToFollwUp(
            @PathVariable("idPartitioning") String idPartitioning,
            @PathVariable("idSu") String idSu) {

        Questioning questioning = questioningService.findByIdPartitioningAndSurveyUnitIdSu(
                idPartitioning, idSu);
        if (questioning == null) {
            throw new NotFoundException(QUESTIONING_DOES_NOT_EXIST);
        }

        Optional<QuestioningEvent> questioningEvent = questioningEventService.getLastQuestioningEvent(questioning,
                TypeQuestioningEvent.FOLLOWUP_EVENTS);

        EligibleDto result = new EligibleDto();
        result.setEligible(questioningEvent.isPresent() ? "false" : "true");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "Add a FOLLWUP state to a questioning")
    @PostMapping(value = Constants.API_WEBCLIENT_FOLLOWUP, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = StateDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @Transactional
    public ResponseEntity<StateDto> postFollwUp(
            @PathVariable("idPartitioning") String idPartitioning,
            @PathVariable("idSu") String idSu) throws JsonProcessingException {

        Questioning questioning = questioningService.findByIdPartitioningAndSurveyUnitIdSu(
                idPartitioning, idSu);
        if (questioning == null) {
            throw new NotFoundException(QUESTIONING_DOES_NOT_EXIST);
        }

        JsonNode node = addWebclientAuthorNode();
        QuestioningEvent questioningEvent = new QuestioningEvent();
        questioningEvent.setQuestioning(questioning);
        questioningEvent.setDate(new Date());
        questioningEvent.setType(TypeQuestioningEvent.FOLLOWUP);
        questioningEvent.setPayload(node);
        questioningEventService.saveQuestioningEvent(questioningEvent);

        questioning.getQuestioningEvents().add(questioningEvent);
        questioningService.saveQuestioning(questioning);

        StateDto result = new StateDto();
        result.setState(questioningEvent.getType().name());
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    private JsonNode addWebclientAuthorNode() throws JsonProcessingException {
        String json = "{\"author\":\"webclient\"}";
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json);
    }

    @Operation(summary = "Indicates whether a questioning should be extract or not (VALINT and PARTIELINT)")
    @GetMapping(value = Constants.API_WEBCLIENT_EXTRACT, produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = EligibleDto.class))),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<EligibleDto> isToExtract(@PathVariable("idPartitioning") String idPartitioning,
                                                   @PathVariable("idSu") String idSu) {

        Questioning questioning = questioningService.findByIdPartitioningAndSurveyUnitIdSu(
                idPartitioning, idSu);
        if (questioning == null) {
            throw new NotFoundException(QUESTIONING_DOES_NOT_EXIST);
        }

        Optional<QuestioningEvent> questioningEvent = questioningEventService.getLastQuestioningEvent(questioning,
                TypeQuestioningEvent.EXTRACT_EVENTS);
        EligibleDto result = new EligibleDto();
        result.setEligible(questioningEvent.isPresent() ? "true" : "false");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
    private Support convertToEntity(SupportDto supportDto) {
        return modelMapper.map(supportDto, Support.class);
    }

    private Owner convertToEntity(OwnerDto ownerDto) {
        return modelMapper.map(ownerDto, Owner.class);
    }

    private Source convertToEntity(SourceDto sourceDto) {
        return modelMapper.map(sourceDto, Source.class);
    }

    private Survey convertToEntity(SurveyDto surveyDto) {
        return modelMapper.map(surveyDto, Survey.class);
    }

    private Campaign convertToEntity(CampaignDto campaignDto) {
        return modelMapper.map(campaignDto, Campaign.class);
    }

    private Partitioning convertToEntity(PartitioningDto partitioningDto) {
        return modelMapper.map(partitioningDto, Partitioning.class);
    }

    private SurveyUnit convertToEntity(SurveyUnitDto surveyUnitDto) {
        return modelMapper.map(surveyUnitDto, SurveyUnit.class);
    }

    private Contact convertToEntity(ContactAccreditationDto contactAccreditationDto) throws NoSuchElementException {
        Contact contact = modelMapper.map(contactAccreditationDto, Contact.class);
        contact.setGender(contactAccreditationDto.getCivility());

        Contact oldContact = contactService.findByIdentifier(contactAccreditationDto.getIdentifier());
        contact.setComment(oldContact.getComment());
        contact.setAddress(oldContact.getAddress());
        contact.setContactEvents(oldContact.getContactEvents());

        return contact;
    }

    private Contact convertToEntityNewContact(ContactAccreditationDto contactAccreditationDto) {
        Contact contact = modelMapper.map(contactAccreditationDto, Contact.class);
        contact.setGender(contactAccreditationDto.getCivility());
        return contact;
    }

    private ContactAccreditationDto convertToDto(Contact contact, boolean isMain) {
        ContactAccreditationDto contactAccreditationDto = modelMapper.map(contact, ContactAccreditationDto.class);
        contactAccreditationDto.setCivility(contact.getGender());
        contactAccreditationDto.setMain(isMain);
        return contactAccreditationDto;
    }

    private SupportDto convertToDto(Support support) {
        return modelMapper.map(support, SupportDto.class);
    }

    private ContactDto convertToDto(Contact contact) {
        ContactDto contactDto = modelMapper.map(contact, ContactDto.class);
        contactDto.setCivility(contact.getGender().name());
        return contactDto;
    }

    private SurveyUnitDto convertToDto(SurveyUnit surveyUnit) {
        return modelMapper.map(surveyUnit, SurveyUnitDto.class);
    }

    private OwnerDto convertToDto(Owner owner) {
        return modelMapper.map(owner, OwnerDto.class);
    }

    private SourceDto convertToDto(Source source) {
        return modelMapper.map(source, SourceDto.class);
    }

    private SurveyDto convertToDto(Survey survey) {
        return modelMapper.map(survey, SurveyDto.class);
    }

    private CampaignDto convertToDto(Campaign campaign) {
        return modelMapper.map(campaign, CampaignDto.class);
    }

    private PartitioningDto convertToDto(Partitioning partitioning) {
        return modelMapper.map(partitioning, PartitioningDto.class);
    }

}
