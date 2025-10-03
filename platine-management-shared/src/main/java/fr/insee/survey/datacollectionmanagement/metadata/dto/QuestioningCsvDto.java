package fr.insee.survey.datacollectionmanagement.metadata.dto;

import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestioningCsvDto {

  private UUID interrogationId;
  private String partitioningId;
  private String surveyUnitId;
  private TypeQuestioningEvent highestEventType;
  private Date highestEventDate;

}