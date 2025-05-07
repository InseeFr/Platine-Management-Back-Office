package fr.insee.survey.datacollectionmanagement.questioning.dto;

import java.util.Set;


public record ContactAccreditedToSurveyUnit (String contactId, boolean isMain, Set<String> campaignIds){}