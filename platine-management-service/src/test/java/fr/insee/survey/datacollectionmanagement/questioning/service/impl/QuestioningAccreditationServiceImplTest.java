package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningAccreditationService;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.ContactServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningAccreditationRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
class QuestioningAccreditationServiceImplTest {

    private QuestioningAccreditationService questioningAccreditationService;

    @BeforeEach
    void init() {

        QuestioningAccreditationRepositoryStub questioningAccreditationRepository = new QuestioningAccreditationRepositoryStub();
        ContactServiceStub contactService = new ContactServiceStub();
        questioningAccreditationService = new QuestioningAccreditationServiceImpl(questioningAccreditationRepository, contactService);
    }

    @Test
    @DisplayName("Should throw error with unknown contact")
    void setQuestioningAccreditationToUnknownContact() {
        Long contactId = Integer.toUnsignedLong(123);
        assertThrows(NotFoundException.class, () ->
            questioningAccreditationService.setQuestioningAccreditationToContact("testId", contactId));
    }

}
