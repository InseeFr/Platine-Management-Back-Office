package fr.insee.survey.datacollectionmanagement.metadata.dto.input;

import fr.insee.modelefiliere.ContextDto;
import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CampaignCreateContextDto(
        @NotBlank String id,
        UUID technicalId,
        int year,
        String campaignWording,
        String period,
        String periodCollect
) {

    public static CampaignCreateContextDto fromContext(ContextDto contextDto) {
        var m = contextDto.getMetadatas();

        return new CampaignCreateContextDto(
                contextDto.getShortLabel(),                     // id
                contextDto.getId(),                             // technicalId (UUID)
                m.getYear() != null ? m.getYear() : 0,          // year (primitive int → 0 si null)
                contextDto.getLabel(),                          // campaignWording
                m.getPeriod().getValue(),                       // period
                m.getPeriod().getValue()                        // period collect
        );
    }
}
