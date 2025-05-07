package fr.insee.survey.datacollectionmanagement.questioning.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class ContactAccreditedToSurveyUnitDto {
    private String contactId;
    private boolean isMain;
    private String campaignIds;

    public static ContactAccreditedToSurveyUnit toRecord(ContactAccreditedToSurveyUnitDto dto) {
        return new ContactAccreditedToSurveyUnit(
                dto.getContactId(),
                dto.isMain(),
                convertToSet(dto.getCampaignIds())
        );
    }

    private static Set<String> convertToSet(String campaignIds) {
        return Arrays.stream(campaignIds.split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }
}