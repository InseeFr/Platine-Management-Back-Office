package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.util.JSONCollectionWrapper;
import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogExtractionRowDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogQuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogSearchDto;
import fr.insee.survey.datacollectionmanagement.query.service.MoogService;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "5 - Moog", description = "Enpoints for moog")
@Slf4j
@RequiredArgsConstructor
public class MoogController {

    private final MoogService moogService;

    private final ContactService contactService;


    @GetMapping(path = UrlConstants.API_MOOG_SEARCH)
    public ResponseEntity<Page<MoogSearchDto>> moogSearch(@RequestParam(required = false) String filter1,
                                                          @RequestParam(required = false) String filter2,
                                                          @RequestParam(defaultValue = "0", required = false) int pageNo,
                                                          @RequestParam(defaultValue = "20", required = false) int pageSize) {

        List<View> listView = moogService.moogSearch(StringUtils.upperCase(filter1));

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), listView.size());

        if (start <= end) {
            Page<MoogSearchDto> page = new PageImpl<>(
                    moogService.transformListViewToListMoogSearchDto(listView.subList(start, end)), pageable,
                    listView.size());
            return new ResponseEntity<>(page, HttpStatus.OK);

        } else
            throw new IllegalArgumentException("Start must be inferior to end");

    }

    @GetMapping(path = UrlConstants.API_MOOG_MAIL, produces = "application/json")
    @Operation(summary = "Get Moog questioning events by campaign and idSu")
    public ResponseEntity<String> getMoogMail(@PathVariable("id") String contactId) {
        Contact contact = contactService.findByIdentifier(contactId);
        return ResponseEntity.ok().body(contact.getEmail());
    }

    @GetMapping(path = UrlConstants.API_MOOG_EVENTS, produces = "application/json")
    @Operation(summary = "Get Moog questioning events by campaign and idSu")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK", content = @Content(array = @ArraySchema(schema = @Schema(implementation = MoogQuestioningEventDto.class)))),
            @ApiResponse(responseCode = "400", description = "Bad Request")
    })
    public ResponseEntity<Map<String, List<MoogQuestioningEventDto>>> getMoogQuestioningEvents(@PathVariable("campaign") String campaignId,
                                                                                               @PathVariable("id") String idSu) {
        return new ResponseEntity<>(Map.of("datas", moogService.getMoogEvents(campaignId, idSu)), HttpStatus.OK);

    }

    @GetMapping(value = UrlConstants.MOOG_API_CAMPAIGN_EXTRACTION, produces = "application/json")
    public JSONCollectionWrapper<MoogExtractionRowDto> provideDataForExtraction(@PathVariable String idCampaign) {
        log.info("Request GET for extraction of campaign : {}", idCampaign);
        return moogService.getExtraction(idCampaign);
    }

    @GetMapping(value = UrlConstants.MOOG_API_CAMPAIGN_SURVEYUNITS_FOLLOWUP, produces = "application/json")
    public JSONCollectionWrapper<MoogExtractionRowDto> displaySurveyUnitsToFollowUp(@PathVariable String idCampaign) {
        log.info("Request GET for su to follow up - campaign {}", idCampaign);
        return new JSONCollectionWrapper<>(moogService.getSurveyUnitsToFollowUp(idCampaign));
    }

    @GetMapping(value = UrlConstants.MOOG_API_READONLY_URL, produces = "application/json")
    public ResponseEntity<String> getReadOnlyUrl(@PathVariable String idCampaign, @PathVariable String surveyUnitId) {
        log.info("Request READONLY url for su {} and campaign {}", surveyUnitId, idCampaign);
        String url;
        try {
            url = moogService.getReadOnlyUrl(idCampaign, surveyUnitId);
            return ResponseEntity.ok().body(url);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
