package fr.insee.survey.datacollectionmanagement.query.dto;

import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;

import java.util.Date;

public record InterrogationStatusEventDto(TypeQuestioningEvent type, Date date) {
}
