package fr.insee.survey.datacollectionmanagement.query.controller;

import fr.insee.survey.datacollectionmanagement.util.JSONCollectionWrapper;
import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogFollowUpDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogProgressDto;
import fr.insee.survey.datacollectionmanagement.query.service.MonitoringService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@Tag(name = "6 - Moog", description = "Enpoints for moog")
@Slf4j
@RequiredArgsConstructor
public class MonitoringController {

    private final MonitoringService monitoringService;

    private final CampaignService campaignService;

    @GetMapping(value = "/api/moog/campaigns/{idCampaign}/monitoring/progress", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONCollectionWrapper<MoogProgressDto> getDataForProgress(@PathVariable String idCampaign) {
        log.info("Request GET for monitoring moog progress table for campaign : {}", idCampaign);
        return monitoringService.getProgress(idCampaign);
    }

    @GetMapping(value = "/api/moog/campaigns/{idCampaign}/monitoring/follow-up", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONCollectionWrapper<MoogFollowUpDto> getDataToFollowUp(@PathVariable String idCampaign) {
        log.info("Request GET for following table for campaign : {}", idCampaign);
        return monitoringService.getFollowUp(idCampaign);
    }

    @GetMapping(value = "/api/temp/moog/campaigns/{idCampaign}/monitoring/progress", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONCollectionWrapper<MoogProgressDto> getDataForProgressTemp(@PathVariable String idCampaign) {
        log.info("Request GET for monitoring moog progress table for campaign : {}", idCampaign);
        Campaign campaign = campaignService.getById(idCampaign);
        log.info("{} partitionings found", campaign.getPartitionings().stream().map(Partitioning::getId)
                .toList().size());
        campaign.getPartitionings().forEach(part -> log.info("{} partitionig found", part.getId()));

        return null;
    }

    @GetMapping(value = "/api/temp/moog/campaigns/{idCampaign}/monitoring/follow-up", produces = MediaType.APPLICATION_JSON_VALUE)
    public JSONCollectionWrapper<MoogFollowUpDto> getDataToFollowUpTemp(@PathVariable String idCampaign) {
        log.info("Request GET for following table for campaign : {}", idCampaign);
        return monitoringService.getFollowUp(idCampaign);
    }
}
