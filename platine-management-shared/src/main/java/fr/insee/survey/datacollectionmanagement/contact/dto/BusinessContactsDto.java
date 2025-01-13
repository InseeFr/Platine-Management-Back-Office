package fr.insee.survey.datacollectionmanagement.contact.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BusinessContactsDto {
    private int hit;
    private int start;
    private int count;
    private List<BusinessContactDto> businessContactDtoList;
}
