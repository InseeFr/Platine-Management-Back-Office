package fr.insee.survey.datacollectionmanagement.questioning.service.builder;

import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningContactDto;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningDetailsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommentOutputDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningCommunicationDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningEventDto;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QuestioningDetailsDtoBuilderTest {

    @Test
    void shouldBuildQuestioningDetailsDtoWithMinimalData() {
        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .questioningId(123L)
                .campaignId("CAMP2025")
                .build();

        assertThat(dto).isNotNull();
        assertThat(dto.getQuestioningId()).isEqualTo(123L);
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
                new QuestioningContactDto("ID1", "Doe", "John"),
                new QuestioningContactDto("ID2", "Smith", "Jane")
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
        event1.setType("EVENT_TYPE_1");
        event1.setEventDate(new Date(1709022000000L));

        QuestioningEventDto event2 = new QuestioningEventDto();
        event2.setType("EVENT_TYPE_2");
        event2.setEventDate(new Date(1709108400000L));

        QuestioningEventDto validatedEvent = new QuestioningEventDto();
        validatedEvent.setType("VALIDATED");
        validatedEvent.setEventDate(new Date(1709194800000L));

        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .events(List.of(event1, event2), event2, validatedEvent)
                .build();

        assertThat(dto.getListEvents()).hasSize(2);
        assertThat(dto.getLastEvent()).isEqualTo("EVENT_TYPE_2");
        assertThat(dto.getDateLastEvent()).isEqualTo(event2.getEventDate());
        assertThat(dto.getValidationDate()).isEqualTo(validatedEvent.getEventDate());
    }

    @Test
    void shouldHandleNullEvents() {
        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .events(null, null, null)
                .build();

        assertThat(dto.getListEvents()).isNull();
        assertThat(dto.getLastEvent()).isNull();
        assertThat(dto.getDateLastEvent()).isNull();
        assertThat(dto.getValidationDate()).isNull();
    }

    @Test
    void shouldHandleEmptyEventsList() {
        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .events(List.of(), null, null)
                .build();

        assertThat(dto.getListEvents()).isEmpty();
        assertThat(dto.getLastEvent()).isNull();
        assertThat(dto.getDateLastEvent()).isNull();
        assertThat(dto.getValidationDate()).isNull();
    }


    @Test
    void shouldSetCommunications() {
        QuestioningCommunicationDto comm1 = new QuestioningCommunicationDto();
        comm1.setType("EMAIL");
        comm1.setDate(new Date(1709022000000L));

        QuestioningCommunicationDto comm2 = new QuestioningCommunicationDto();
        comm2.setType("PHONE_CALL");
        comm2.setDate(new Date(1709108400000L));

        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .communications(List.of(comm1, comm2))
                .build();

        assertThat(dto.getListCommunications()).hasSize(2);
        assertThat(dto.getLastCommunication()).isEqualTo("PHONE_CALL");
        assertThat(dto.getDateLastCommunication()).isEqualTo(comm2.getDate());
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
        assertThat(dto.getListComments().get(0).getComment()).isEqualTo("This is a comment.");
        assertThat(dto.getListComments().get(0).getAuthor()).isEqualTo("John Doe");
        assertThat(dto.getListComments().get(0).getCommentDate()).isEqualTo(comment.getCommentDate());
    }

    @Test
    void shouldSetReadOnlyUrl() {
        QuestioningDetailsDto dto = new QuestioningDetailsDtoBuilder()
                .readOnlyUrl("https://example.com/readOnly")
                .build();

        assertThat(dto.getReadOnlyUrl()).isEqualTo("https://example.com/readOnly");
    }
}