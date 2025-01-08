package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeCommunicationEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.impl.QuestioningCommunicationServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class QuestioningCommunicationServiceTest {

    static QuestioningCommunicationService questioningCommunicationService;

    @BeforeAll
    static void init() {
        questioningCommunicationService = new QuestioningCommunicationServiceImpl(new ModelMapper());
    }

    @Test
    @DisplayName("Get last communication with 0 communication")
    void getLastQuestioningCommunication0() {
        Questioning questioning = new Questioning();
        Set<QuestioningCommunication> questioningCommunications = new HashSet<>();
        questioning.setQuestioningCommunications(questioningCommunications);

        assertEquals(Optional.empty(), questioningCommunicationService.getLastQuestioningCommunication(questioning));
    }

    @Test
    @DisplayName("Get last communication with 1 communication")
    void getLastQuestioningCommunication1() {
        Questioning questioning = new Questioning();

        Set<QuestioningCommunication> questioningCommunications = new HashSet<>();
        QuestioningCommunication questioningCommunicationOuv = initQuestioningCommunication(TypeCommunicationEvent.COURRIER_OUVERTURE, new Date());
        questioningCommunications.add(questioningCommunicationOuv);

        questioning.setQuestioningCommunications(questioningCommunications);

        Optional<QuestioningCommunication> optionalQuestioningCommunication = questioningCommunicationService.getLastQuestioningCommunication(questioning);
        assertNotEquals(Optional.empty(), optionalQuestioningCommunication);
        assertEquals(TypeCommunicationEvent.COURRIER_OUVERTURE, optionalQuestioningCommunication.get().getType());
    }


    @Test
    @DisplayName("Get last communication with 2 communications")
    void getLastQuestioningCommunication2() {
        Questioning questioning = new Questioning();

        Set<QuestioningCommunication> questioningCommunications = new HashSet<>();
        QuestioningCommunication questioningCommunicationOuv = initQuestioningCommunication(TypeCommunicationEvent.COURRIER_OUVERTURE, Date.from(Instant.now().minus(1, ChronoUnit.DAYS)));
        QuestioningCommunication questioningCommunicationRel = initQuestioningCommunication(TypeCommunicationEvent.COURRIER_RELANCE, new Date());

        questioningCommunications.add(questioningCommunicationOuv);
        questioningCommunications.add(questioningCommunicationRel);


        questioning.setQuestioningCommunications(questioningCommunications);

        Optional<QuestioningCommunication> optionalQuestioningCommunication = questioningCommunicationService.getLastQuestioningCommunication(questioning);
        assertNotEquals(Optional.empty(), optionalQuestioningCommunication);
        assertEquals(TypeCommunicationEvent.COURRIER_RELANCE, optionalQuestioningCommunication.get().getType());
    }

    private static QuestioningCommunication initQuestioningCommunication(TypeCommunicationEvent typeCommunicationEvent, Date date) {
        QuestioningCommunication questioningCommunication = new QuestioningCommunication();
        questioningCommunication.setDate(date);
        questioningCommunication.setType(typeCommunicationEvent);
        return questioningCommunication;
    }

}