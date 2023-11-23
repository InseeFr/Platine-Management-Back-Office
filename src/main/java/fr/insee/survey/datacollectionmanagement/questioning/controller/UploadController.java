package fr.insee.survey.datacollectionmanagement.questioning.controller;

import fr.insee.survey.datacollectionmanagement.config.JSONCollectionWrapper;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
import fr.insee.survey.datacollectionmanagement.exception.RessourceNotValidatedException;
import fr.insee.survey.datacollectionmanagement.query.domain.ResultUpload;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Upload;
import fr.insee.survey.datacollectionmanagement.questioning.dto.UploadDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningEventService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.UploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@PreAuthorize("@AuthorizeMethodDecider.isInternalUser() "
        + "|| @AuthorizeMethodDecider.isWebClient() "
        + "|| @AuthorizeMethodDecider.isAdmin() ")
@Tag(name = "5 - Moog", description = "Enpoints for moog")
@Slf4j
@RequiredArgsConstructor
public class UploadController {

    private final UploadService moogUploadService;

    private final QuestioningEventService questioningEventService;

    private final QuestioningService questioningService;

    @DeleteMapping(value = Constants.MOOG_API_UPLOADS_ID)
    public ResponseEntity<?> deleteOneUpload(@PathVariable Long id) {
        log.info("Request DELETE for upload n° {}", id);

        Optional<Upload> upOpt = moogUploadService.findById(id);
        if(!upOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Upload does not exist");
        }
        Upload up = upOpt.get();
        up.getQuestioningEvents().stream().forEach(q -> {
            Questioning quesitoning = q.getQuestioning();
            quesitoning.setQuestioningEvents(quesitoning.getQuestioningEvents().stream()
                    .filter(qe -> !qe.equals(q)).collect(Collectors.toSet()));
            questioningService.saveQuestioning(quesitoning);
            questioningEventService.deleteQuestioningEvent(q.getId());
        });
        moogUploadService.delete(up);

        return new ResponseEntity<Upload>(HttpStatus.NO_CONTENT);


    }

    @GetMapping(value = Constants.MOOG_API_CAMPAIGN_UPLOADS, produces = "application/json")
    public JSONCollectionWrapper<Upload> displayAllUploads(@PathVariable String idCampaign) {
        log.info("Request GET for uploads");
        return new JSONCollectionWrapper<Upload>(moogUploadService.findAllByIdCampaign(idCampaign));
    }

    @PostMapping(value = Constants.MOOG_API_CAMPAIGN_UPLOADS, produces = "application/json")
    public ResultUpload addQuestioningEventViaUpload(@PathVariable String idCampaign,
                                                             @RequestBody UploadDto request) throws RessourceNotValidatedException {
        log.info("Request POST to add an upload");
        ResultUpload retourInfo = moogUploadService.save(idCampaign, request);
        return retourInfo;
    }

}
