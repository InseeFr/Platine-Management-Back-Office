package fr.insee.survey.datacollectionmanagement.questioning.service.builder;

import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningContactDto;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommentOutputDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestioningDetailsDtoBuilderTest {

    @Test
    void shouldBuildQuestioningDetailsDtoWithMinimalData() {
        UUID questioningId = UUID.randomUUID();
        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .questioningId(questioningId)
                .campaignId("CAMP2025")
                .build();

        assertThat(dto).isNotNull();
        assertThat(dto.getQuestioningId()).isEqualTo(questioningId);
        assertThat(dto.getCampaignId()).isEqualTo("CAMP2025");
    }

    @Test
    void shouldSetSurveyUnit() {
        QuestioningSurveyUnitDto surveyUnit = new QuestioningSurveyUnitDto(
                "SU123", "ID-001", "Unit Name", "Survey Unit Label"
        );

        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .surveyUnit(surveyUnit)
                .build();

        assertThat(dto.getSurveyUnitId()).isEqualTo("SU123");
        assertThat(dto.getSurveyUnitIdentificationCode()).isEqualTo("ID-001");
        assertThat(dto.getSurveyUnitIdentificationName()).isEqualTo("Unit Name");
        assertThat(dto.getSurveyUnitLabel()).isEqualTo("Survey Unit Label");
    }

    @Test
    void shouldHandleNullSurveyUnit() {
        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .surveyUnit(null)
                .build();

        assertThat(dto.getSurveyUnitId()).isNull();
        assertThat(dto.getSurveyUnitIdentificationCode()).isNull();
        assertThat(dto.getSurveyUnitIdentificationName()).isNull();
        assertThat(dto.getSurveyUnitLabel()).isNull();
    }

    @Test
    void shouldSetContacts() {
        List<QuestioningContactDto> contacts = List.of(
                new QuestioningContactDto("ID1", "Doe", "John", false),
                new QuestioningContactDto("ID2", "Smith", "Jane", true)
        );

        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .contacts(contacts)
                .build();

        assertThat(dto.getListContacts()).hasSize(2);
        assertThat(dto.getListContacts()).containsExactlyInAnyOrder(contacts.get(0), contacts.get(1));

    }

    @Test
    void shouldSetEvents() {
        QuestioningEventDto event1 = new QuestioningEventDto();
        event1.setType("INIT_LA");
        event1.setEventDate(new Date(1709022000000L));

        QuestioningEventDto event2 = new QuestioningEventDto();
        event2.setType("VALINT");
        event2.setEventDate(new Date(1709108400000L));

        QuestioningEventDto validatedEvent = new QuestioningEventDto();
        validatedEvent.setType("VALID");
        validatedEvent.setEventDate(new Date(1709194800000L));

        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .events(List.of(event1, event2), TypeQuestioningEvent.valueOf(event2.getType()), event2.getEventDate(), validatedEvent)
                .build();

        assertThat(dto.getListEvents()).hasSize(2);
        assertThat(dto.getLastEventId()).isEqualTo(event2.getId());
        assertThat(dto.getLastEvent()).isEqualTo("VALINT");
        assertThat(dto.getDateLastEvent()).isEqualTo(event2.getEventDate());
        assertThat(dto.getValidationDate()).isEqualTo(validatedEvent.getEventDate());
    }

    @Test
    void shouldHandleNullEvents() {
        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .events(null, null, null, null)
                .build();

        assertThat(dto.getListEvents()).isNull();
        assertThat(dto.getLastEventId()).isNull();
        assertThat(dto.getLastEvent()).isNull();
        assertThat(dto.getDateLastEvent()).isNull();
        assertThat(dto.getValidationDate()).isNull();
    }

    @Test
    void shouldHandleEmptyEventsList() {
        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .events(List.of(), null, null, null)
                .build();

        assertThat(dto.getListEvents()).isEmpty();
        assertThat(dto.getLastEventId()).isNull();
        assertThat(dto.getLastEvent()).isNull();
        assertThat(dto.getDateLastEvent()).isNull();
        assertThat(dto.getValidationDate()).isNull();
    }


    @Test
    void shouldSetCommunications() {
        QuestioningCommunicationDto comm1 = new QuestioningCommunicationDto();
        comm1.setType("EMAIL");
        comm1.setDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(1709022000000L), ZoneId.systemDefault()));
        QuestioningCommunicationDto comm2 = new QuestioningCommunicationDto();
        comm2.setType("PHONE_CALL");
        comm2.setDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(1709108400000L), ZoneId.systemDefault()));
        QuestioningCommunicationDto comm3 = new QuestioningCommunicationDto();
        comm3.setType("RELANCE");
        comm3.setDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(1709108400000L), ZoneId.systemDefault()));
        comm3.setWithQuestionnaire(true);
        comm3.setWithReceipt(true);

        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .communications(List.of(comm1, comm2, comm3))
                .build();

        assertThat(dto.getListCommunications()).hasSize(3);
        assertThat(dto.getLastCommunication()).isEqualTo("PHONE_CALL");
        assertThat(dto.getDateLastCommunication()).isEqualTo(comm2.getDate());
        assertFalse(dto.getListCommunications().get(1).isWithReceipt());
        assertFalse(dto.getListCommunications().get(1).isWithReceipt());
        assertTrue(dto.getListCommunications().get(2).isWithQuestionnaire());
        assertTrue(dto.getListCommunications().get(2).isWithReceipt());

    }

    @Test
    void shouldHandleNullCommunications() {
        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .communications(null)
                .build();

        assertThat(dto.getListCommunications()).isNull();
        assertThat(dto.getLastCommunication()).isNull();
        assertThat(dto.getDateLastCommunication()).isNull();
    }

    @Test
    void shouldHandleEmptyCommunicationsList() {
        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .communications(List.of())
                .build();

        assertThat(dto.getListCommunications()).isEmpty();
        assertThat(dto.getLastCommunication()).isNull();
        assertThat(dto.getDateLastCommunication()).isNull();
    }

    @Test
    void shouldSetComments() {
        QuestioningCommentOutputDto comment = new QuestioningCommentOutputDto();
        comment.setComment("This is a comment.");
        comment.setAuthor("John Doe");
        comment.setCommentDate(new Date(1709022000000L));

        List<QuestioningCommentOutputDto> comments = List.of(comment);

        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .comments(comments)
                .build();

        assertThat(dto.getListComments()).hasSize(1);
        assertThat(dto.getListComments().getFirst().getComment()).isEqualTo("This is a comment.");
        assertThat(dto.getListComments().getFirst().getAuthor()).isEqualTo("John Doe");
        assertThat(dto.getListComments().getFirst().getCommentDate()).isEqualTo(comment.getCommentDate());
    }

    @Test
    void shouldSetReadOnlyUrl() {
        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .readOnlyUrl("https://example.com/readOnly")
                .build();

        assertThat(dto.getReadOnlyUrl()).isEqualTo("https://example.com/readOnly");
    }

    @Test
    void shouldSetIsHousehold() {
        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .isHousehold(true)
                .build();

        assertThat(dto.getIsHousehold()).isTrue();

        dto = new QuestioningDetailsDtoBuilder()
                .isHousehold(false)
                .build();

        assertThat(dto.getIsHousehold()).isFalse();

        dto = new QuestioningDetailsDtoBuilder()
                .isHousehold(null)
                .build();

        assertThat(dto.getIsHousehold()).isNull();
    }

    @Test
    void shouldNotSetHighestEventIfTypeOrDateIsNull() {
        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .events(null, TypeQuestioningEvent.EXPERT, null, null)
                .build();

        assertThat(dto.getDateLastEvent()).isNull();
        assertThat(dto.getValidationDate()).isNull();

        dto = new QuestioningDetailsDtoBuilder()
                .events(null, null, new Date(), null)
                .build();

        assertThat(dto.getDateLastEvent()).isNull();
        assertThat(dto.getValidationDate()).isNull();
    }
}