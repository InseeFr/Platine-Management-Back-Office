package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.util.JSONCollectionWrapper;
import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.dto.CampaignMoogDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@Slf4j
@Tag(name = "6 - Moog", description = "Enpoints for moog")
@PreAuthorize(AuthorityPrivileges.HAS_MANAGEMENT_PRIVILEGES)
@RequiredArgsConstructor
public class MetadataController {

    private final CampaignService campaignService;

    private final PartitioningService partitioningService;

    @GetMapping(value = UrlConstants.MOOG_API_CAMPAIGNS)
    public JSONCollectionWrapper<CampaignMoogDto> displayCampaignInProgress() {
        log.info("Request GET campaigns");
        return new JSONCollectionWrapper<CampaignMoogDto>(campaignService.getCampaigns());
    }

    @PutMapping(value = UrlConstants.MOOG_API_CAMPAIGNS_ID)
    @PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
    public void updateCampaignInProgressMoog(@PathVariable("id") String id, @RequestBody CampaignMoogDto campaignMoogDto) {
        log.info("Updating Moog campaign with id {}", id);
        Campaign campaign = campaignService.findById(id);
        campaign.getPartitionings().forEach(p->{
            p.setClosingDate(new Date(campaignMoogDto.getCollectionEndDate()));
            p.setOpeningDate(new Date(campaignMoogDto.getCollectionStartDate()));
            partitioningService.insertOrUpdatePartitioning(p);
        });
        campaign.setCampaignWording(campaignMoogDto.getLabel());
        campaignService.insertOrUpdateCampaign(campaign);
    }

}
