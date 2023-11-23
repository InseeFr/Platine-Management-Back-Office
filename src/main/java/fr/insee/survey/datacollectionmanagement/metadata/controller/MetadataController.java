package fr.insee.survey.datacollectionmanagement.metadata.controller;

import fr.insee.survey.datacollectionmanagement.config.JSONCollectionWrapper;
import fr.insee.survey.datacollectionmanagement.constants.Constants;
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
import java.util.Optional;

@RestController
@Slf4j
@Tag(name = "5 - Moog", description = "Enpoints for moog")
@PreAuthorize("@AuthorizeMethodDecider.isInternalUser() "
        + "|| @AuthorizeMethodDecider.isWebClient() "
        + "|| @AuthorizeMethodDecider.isAdmin() ")
@RequiredArgsConstructor
public class MetadataController {

    private final CampaignService campaignService;

    private final PartitioningService partitioningService;

    @GetMapping(value = Constants.MOOG_API_CAMPAIGNS)
    public JSONCollectionWrapper<CampaignMoogDto> displayCampaignInProgress() {
        log.info("Request GET campaigns");
        return new JSONCollectionWrapper<CampaignMoogDto>(campaignService.getCampaigns());
    }

    @PutMapping(value = Constants.MOOG_API_CAMPAIGNS_ID)
    public void updateCampaignInProgressMoog(@PathVariable("id") String id, @RequestBody CampaignMoogDto campaignMoogDto) {
        log.info("Updating Moog campaign with id " + id);
        Optional<Campaign> campaign = campaignService.findById(id);
        if(!campaign.isPresent())
        {

        }
        campaign.get().getPartitionings().stream().forEach(p->{
            p.setClosingDate(new Date(campaignMoogDto.getCollectionEndDate()));
            p.setOpeningDate(new Date(campaignMoogDto.getCollectionStartDate()));
            partitioningService.insertOrUpdatePartitioning(p);
        });
        campaign.get().setCampaignWording(campaignMoogDto.getLabel());
        campaignService.insertOrUpdateCampaign(campaign.get());
    }

}
