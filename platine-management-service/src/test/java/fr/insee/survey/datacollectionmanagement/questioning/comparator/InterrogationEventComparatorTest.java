package fr.insee.survey.datacollectionmanagement.questioning.comparator;

import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.InterrogationEventOrderRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class InterrogationEventComparatorTest {

    private InterrogationEventComparator comparator;

    @BeforeEach
    void setUp() {
        InterrogationEventOrderRepositoryStub orderRepository = new InterrogationEventOrderRepositoryStub();
        comparator = new InterrogationEventComparator(orderRepository);
    }

    @ParameterizedTest(name = "#{index}: expected highest = {1}")
    @MethodSource("acceptanceScenarios")
    @DisplayName("should return the event with the highest priority")
    void should_select_correct_event(List<QuestioningEvent> events,
                                     TypeQuestioningEvent expectedWinner) {

        QuestioningEvent winner = events.stream()
                .max(comparator)
                .orElseThrow();

        assertThat(winner.getType()).isEqualTo(expectedWinner);
    }

    private static Stream<Arguments> acceptanceScenarios() {
        return Stream.of(
                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA)),
                        TypeQuestioningEvent.INITLA),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.REFUSAL)),
                        TypeQuestioningEvent.REFUSAL),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.WASTE)),
                        TypeQuestioningEvent.WASTE),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.HC)),
                        TypeQuestioningEvent.HC),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT)),
                        TypeQuestioningEvent.PARTIELINT),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT),
                                event(TypeQuestioningEvent.REFUSAL)),
                        TypeQuestioningEvent.REFUSAL),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT),
                                event(TypeQuestioningEvent.WASTE)),
                        TypeQuestioningEvent.WASTE),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT),
                                event(TypeQuestioningEvent.HC)),
                        TypeQuestioningEvent.HC),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT, -2),
                                event(TypeQuestioningEvent.VALPAP, -1),
                                event(TypeQuestioningEvent.VALINT, 0)),
                        TypeQuestioningEvent.VALINT),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT,-2),
                                event(TypeQuestioningEvent.VALPAP,  0),
                                event(TypeQuestioningEvent.VALINT, -1)),
                        TypeQuestioningEvent.VALPAP),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT),
                                event(TypeQuestioningEvent.VALPAP),
                                event(TypeQuestioningEvent.VALINT),
                                event(TypeQuestioningEvent.REFUSAL)),
                        TypeQuestioningEvent.REFUSAL),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT),
                                event(TypeQuestioningEvent.VALPAP),
                                event(TypeQuestioningEvent.VALINT),
                                event(TypeQuestioningEvent.WASTE)),
                        TypeQuestioningEvent.WASTE),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT),
                                event(TypeQuestioningEvent.VALPAP),
                                event(TypeQuestioningEvent.VALINT),
                                event(TypeQuestioningEvent.HC)),
                        TypeQuestioningEvent.HC),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.REFUSAL, -2),
                                event(TypeQuestioningEvent.WASTE,    0)),
                        TypeQuestioningEvent.WASTE),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.WASTE,    -2),
                                event(TypeQuestioningEvent.REFUSAL,  0)),
                        TypeQuestioningEvent.REFUSAL),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.WASTE),
                                event(TypeQuestioningEvent.HC)),
                        TypeQuestioningEvent.HC),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT),
                                event(TypeQuestioningEvent.REFUSAL, -1),
                                event(TypeQuestioningEvent.WASTE,    0)),
                        TypeQuestioningEvent.WASTE),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT),
                                event(TypeQuestioningEvent.REFUSAL,  0),
                                event(TypeQuestioningEvent.WASTE,   -1)),
                        TypeQuestioningEvent.REFUSAL),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT),
                                event(TypeQuestioningEvent.REFUSAL),
                                event(TypeQuestioningEvent.HC)),
                        TypeQuestioningEvent.HC),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT),
                                event(TypeQuestioningEvent.WASTE),
                                event(TypeQuestioningEvent.HC)),
                        TypeQuestioningEvent.HC),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT),
                                event(TypeQuestioningEvent.VALPAP),
                                event(TypeQuestioningEvent.VALINT),
                                event(TypeQuestioningEvent.REFUSAL, -1),
                                event(TypeQuestioningEvent.WASTE, 0)),
                        TypeQuestioningEvent.WASTE),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT),
                                event(TypeQuestioningEvent.VALPAP),
                                event(TypeQuestioningEvent.VALINT),
                                event(TypeQuestioningEvent.WASTE,-1),
                                event(TypeQuestioningEvent.REFUSAL, 0)),
                        TypeQuestioningEvent.REFUSAL),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT),
                                event(TypeQuestioningEvent.VALPAP),
                                event(TypeQuestioningEvent.VALINT),
                                event(TypeQuestioningEvent.REFUSAL),
                                event(TypeQuestioningEvent.HC)),
                        TypeQuestioningEvent.HC),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT),
                                event(TypeQuestioningEvent.VALPAP),
                                event(TypeQuestioningEvent.VALINT),
                                event(TypeQuestioningEvent.WASTE),
                                event(TypeQuestioningEvent.HC)),
                        TypeQuestioningEvent.HC),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.PARTIELINT),
                                event(TypeQuestioningEvent.REFUSAL),
                                event(TypeQuestioningEvent.WASTE),
                                event(TypeQuestioningEvent.HC)),
                        TypeQuestioningEvent.HC),

                scenario(
                        List.of(event(TypeQuestioningEvent.PARTIELINT,0),
                                event(TypeQuestioningEvent.VALPAP,-1),
                                event(TypeQuestioningEvent.VALINT,-2)),
                        TypeQuestioningEvent.PARTIELINT),

                scenario(
                        List.of(event(TypeQuestioningEvent.INITLA),
                                event(TypeQuestioningEvent.EXPERT)),
                        TypeQuestioningEvent.EXPERT),

                scenario(
                        List.of(event(TypeQuestioningEvent.ONGEXPERT,0),
                                event(TypeQuestioningEvent.VALID,-1),
                                event(TypeQuestioningEvent.EXPERT,-2)),
                        TypeQuestioningEvent.ONGEXPERT),

                scenario(
                        List.of(event(TypeQuestioningEvent.VALID,0),
                                event(TypeQuestioningEvent.ONGEXPERT,-1),
                                event(TypeQuestioningEvent.EXPERT,-2)),
                        TypeQuestioningEvent.VALID),

                scenario(
                        List.of(event(TypeQuestioningEvent.ONGEXPERT,0),
                                event(TypeQuestioningEvent.ENDEXPERT,-1)),
                        TypeQuestioningEvent.ONGEXPERT),

                scenario(
                        List.of(event(TypeQuestioningEvent.ENDEXPERT,0),
                                event(TypeQuestioningEvent.VALID,-1)),
                        TypeQuestioningEvent.ENDEXPERT)

        );
    }

    private static QuestioningEvent event(TypeQuestioningEvent type, int offsetDays) {
        LocalDate base = LocalDate.now().plusDays(offsetDays);
        Date date = Date.from(base.atStartOfDay(ZoneId.systemDefault()).toInstant());
        return new QuestioningEvent(date, type, null);
    }

    private static QuestioningEvent event(TypeQuestioningEvent type) {
        return event(type, 0);
    }

    private static Arguments scenario(List<QuestioningEvent> data, TypeQuestioningEvent expected) {
        return Arguments.of(data, expected);
    }

}
