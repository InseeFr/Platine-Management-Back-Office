package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.ContactServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningAccreditationRepositoryStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningServiceStub;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
class QuestioningAccreditationServiceImplTest {

    private QuestioningAccreditationService questioningAccreditationService;
    private QuestioningAccreditationRepositoryStub questioningAccreditationRepository;
    private ContactServiceStub contactService;
    private QuestioningServiceStub questioningService;

    @BeforeEach
    void init() {
        questioningAccreditationRepository = new QuestioningAccreditationRepositoryStub();
        contactService = new ContactServiceStub();
        questioningService = new QuestioningServiceStub();
        questioningAccreditationService = new QuestioningAccreditationServiceImpl(questioningAccreditationRepository, contactService, questioningService);
    }

    @Test
    @DisplayName("Should throw error with unknown contact")
    void setQuestioningAccreditationToUnknownContact() {
        String contactId = "testId";
        assertThatThrownBy(
                () -> questioningAccreditationService.setQuestioningAccreditationToContact(contactId, 123L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(String.format("Contact %s not found", contactId));
    }

    @Test
    @DisplayName("Should throw error with unknown interrogation")
    void setQuestioningAccreditationToUnknownInterrogation() {

        Long questioningId = 123L;
        String contactId = "testId";

        Contact contact = new Contact();
        contact.setIdentifier(contactId);
        contactService.saveContact(contact);

        assertThatThrownBy(
                () -> questioningAccreditationService.setQuestioningAccreditationToContact(contactId, questioningId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage(String.format("Questioning %s not found", questioningId));
    }

    @Test
    @DisplayName("Should set questioning accreditation to specified contact")
    void setQuestioningAccreditationToContact() {
        QuestioningAccreditation qa = new QuestioningAccreditation();
        Long questioningId = 123L;
        String contactId = "testId";

        Contact contact = new Contact();
        contact.setIdentifier(contactId);
        contactService.saveContact(contact);

        qa.setId(1L);
        qa.setIdContact("otherId");
        qa.setMain(true);

        Questioning questioning = new Questioning();
        questioning.setId(questioningId);
        questioningService.saveQuestioning(questioning);

        qa.setQuestioning(questioning);

        questioningAccreditationRepository.save(qa);
        questioningAccreditationService.setQuestioningAccreditationToContact(contactId, questioningId);

        assertThat(qa.getIdContact()).isEqualTo(contactId);
    }
}
