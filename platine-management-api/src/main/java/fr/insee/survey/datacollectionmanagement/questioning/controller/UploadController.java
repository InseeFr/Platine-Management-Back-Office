package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.exception.RessourceNotValidatedException;
import fr.insee.survey.datacollectionmanagement.query.domain.ResultUpload;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Upload;
import fr.insee.survey.datacollectionmanagement.questioning.dto.UploadDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.UploadService;
import fr.insee.survey.datacollectionmanagement.util.JSONCollectionWrapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "6 - Moog", description = "Enpoints for moog")
@Slf4j
@RequiredArgsConstructor
public class UploadController {

    private final UploadService moogUploadService;

    private final QuestioningEventService questioningEventService;

    private final QuestioningService questioningService;

    @DeleteMapping(value = UrlConstants.MOOG_API_UPLOADS_ID)
    public ResponseEntity<Upload> deleteOneUpload(@PathVariable Long id) {
        log.info("Request DELETE for upload n° {}", id);

        Upload up = moogUploadService.findById(id);
        up.getQuestioningEvents().forEach(q ->
            questioningEventService.deleteQuestioningEvent(q.getId())
        );
        moogUploadService.delete(up);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);


    }

    @GetMapping(value = UrlConstants.MOOG_API_CAMPAIGN_UPLOADS, produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONCollectionWrapper<Upload> displayAllUploads(@PathVariable String idCampaign) {
        log.info("Request GET for uploads");
        return new JSONCollectionWrapper<>(moogUploadService.findAllByIdCampaign(idCampaign));
    }

    @PostMapping(value = UrlConstants.MOOG_API_CAMPAIGN_UPLOADS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResultUpload addQuestioningEventViaUpload(@PathVariable String idCampaign,
                                                             @RequestBody UploadDto request) throws RessourceNotValidatedException {
        log.info("Request POST to add an upload");
        return moogUploadService.save(idCampaign, request);
    }

}
