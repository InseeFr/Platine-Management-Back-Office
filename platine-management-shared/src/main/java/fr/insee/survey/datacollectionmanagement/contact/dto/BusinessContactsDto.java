package fr.insee.survey.datacollectionmanagement.contact.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class BusinessContactsDto {
    private int hit;
    private int start;
    private int count;
    @JsonProperty("contact")
    private List<BusinessContactDto> businessContactDtoList;
}
