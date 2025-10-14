package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.modelefiliere.ContextDto;
import fr.insee.modelefiliere.ModeDto;
import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.metadata.dto.input.*;
import fr.insee.survey.datacollectionmanagement.metadata.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES + " || " + AuthorityPrivileges.HAS_WEBCLIENT_PRIVILEGES)
@Tag(name = "3 - Metadata", description = "Enpoints to create, update, delete and find entities in metadata domain")
@Slf4j
@Validated
@RequiredArgsConstructor
public class ContextController {
    private final ContextService contextService;

    @Operation(summary = "Post context")
    @PostMapping(value = "/api/context", produces = "application/json")
    public void postContext(@RequestBody @Valid ContextDto contextDto){
        SourceCreateContextDto source = SourceCreateContextDto.fromContextMetadatas(contextDto);
        SurveyCreateContextDto survey = SurveyCreateContextDto.fromContextMetadatas(contextDto);
        CampaignCreateContextDto campaign = CampaignCreateContextDto.fromContext(contextDto);
        List<PartitioningCreateContextDto> partitionings = contextDto.getPartitions()
                .stream()
                .filter(partitionDto -> partitionDto.getPartitionModes().contains(ModeDto.CAWI))
                .map(PartitioningCreateContextDto::fromPartitionDto)
                .toList();

        ContextCreateDto contextCreateDto = new ContextCreateDto(survey, source, campaign, partitionings);
        contextService.saveContext(contextCreateDto);
    }
}

