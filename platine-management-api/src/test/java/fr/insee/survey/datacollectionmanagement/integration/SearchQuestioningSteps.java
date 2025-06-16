package fr.insee.survey.datacollectionmanagement.integration;

import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchQuestioningParams;
import fr.insee.survey.datacollectionmanagement.questioning.enums.StatusCommunication;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeCommunicationEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningEventRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.impl.QuestioningServiceImpl;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchQuestioningSteps {
    @Autowired
    QuestioningServiceImpl questioningService;

    @Autowired
    QuestioningRepository questioningRepository;

    @Autowired
    QuestioningEventRepository questioningEventRepository;

    @Autowired
    SurveyUnitRepository surveyUnitRepository;

    @Autowired
    private QuestioningContext questioningContext;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private Slice<SearchQuestioningDto> resultPage;

    @Transactional
    @Given("the questioning {int} for partitioning {string} and survey unit id {string} and model {string}")
    public void createQuestioning(int id, String partId, String idSu, String model) {
        Optional<Questioning> q = questioningRepository.findByIdPartitioningAndSurveyUnitIdSu(partId, idSu);

        if (q.isPresent()) {
            questioningContext.registerQuestioning(id, q.get().getId());
            return;
        }

        Questioning questioning = new Questioning();
        questioning.setIdPartitioning(partId);
        questioning.setModelName(model);
        SurveyUnit su = surveyUnitRepository.findById(idSu).orElseThrow(() -> new IllegalArgumentException("Survey Unit not found"));
        questioning.setSurveyUnit(su);
        questioningRepository.save(questioning);
        questioningContext.registerQuestioning(id, questioning.getId());
    }

    @Transactional
    @Given("the questioning event for questioning {int} with type {string} and date {string}")
    public void createQuestioningEvent(int questioningId, String type, String isoDate) throws ParseException {
        Date date = sdf.parse(isoDate);
        Long realId = questioningContext.getRealId(questioningId);
        Questioning questioning = questioningRepository.getReferenceById(realId);
        QuestioningEvent qe = new QuestioningEvent(date, TypeQuestioningEvent.valueOf(type), questioning);
        questioning.getQuestioningEvents().add(qe);
        questioningEventRepository.save(qe);
        questioningRepository.save(questioning);
    }

    @Transactional
    @Given("the questioning communication for questioning {int} with type {string} and date {string}")
    public void createQuestioningCommunication(int questioningId, String type, String isoDate) throws ParseException {
        Date date = sdf.parse(isoDate);
        Long realId = questioningContext.getRealId(questioningId);
        Questioning questioning = questioningRepository.getReferenceById(realId);
        QuestioningCommunication qc = new QuestioningCommunication(date, TypeCommunicationEvent.valueOf(type), questioning, StatusCommunication.MANUAL);
        questioning.getQuestioningCommunications().add(qc);
        questioningRepository.save(questioning);
    }

    @When("I search for Questioning with {string} and page {int} with size {int}")
    public void iSearchForQuestioningWithSurveyUnitId(String surveyUnitId, int page, int size) {
        SearchQuestioningParams searchQuestioningParams = new SearchQuestioningParams(surveyUnitId, null, null, null);

        resultPage = questioningService.searchQuestionings(
                searchQuestioningParams, PageRequest.of(page, size)
        );
    }

    @When("I search questionings for campaign {string} and highest event types")
    public void i_search_questionings_for_campaign_and_highest_event_type(String campaignId, List<String> stringTypes) {
        List<TypeQuestioningEvent> types = stringTypes.stream()
                .map(TypeQuestioningEvent::valueOf)
                .toList();
        SearchQuestioningParams searchQuestioningParams = new SearchQuestioningParams(null, List.of(campaignId), types, null);
        searchQuestionings(searchQuestioningParams);
    }

    @When("I search questionings for campaign {string} and last communication types")
    public void i_search_questionings_for_campaign_and_last_communication_type(String campaignId, List<String> stringTypes) {
        List<TypeCommunicationEvent> types = stringTypes.stream()
                .map(TypeCommunicationEvent::valueOf)
                .toList();
        SearchQuestioningParams searchQuestioningParams = new SearchQuestioningParams(null, List.of(campaignId), null, types);
        searchQuestionings(searchQuestioningParams);
    }

    @When("I search questionings for campaign {string} and highest event type {string} and last communication type {string}")
    public void i_search_questionings_for_campaign_and_highest_event_type_and_last_communication_type(String campaignId, String highestEventType, String lastCommunicationType) {
        TypeQuestioningEvent highestEventTypeEnum = TypeQuestioningEvent.valueOf(highestEventType);
        TypeCommunicationEvent lastCommunicationTypeEnum = TypeCommunicationEvent.valueOf(lastCommunicationType);
        SearchQuestioningParams searchQuestioningParams = new SearchQuestioningParams(null, List.of(campaignId), List.of(highestEventTypeEnum), List.of(lastCommunicationTypeEnum));
        searchQuestionings(searchQuestioningParams);
    }

    private void searchQuestionings(SearchQuestioningParams searchQuestioningParams) {
        resultPage = questioningService.searchQuestionings(
                searchQuestioningParams, PageRequest.of(0, 20)
        );
    }

    @When("I search for all Questioning with page {int} and size {int}")
    public void iSearchForAllQuestioningWithPageAndSize(int page, int size) {
        SearchQuestioningParams searchQuestioningParams = new SearchQuestioningParams(null, null, null, null);

        resultPage = questioningService.searchQuestionings(
                searchQuestioningParams, PageRequest.of(page, size)
        );
    }

    @Then("the result should contain the following Questioning related to surveyUnit:")
    public void theResultShouldContainTheFollowingQuestioningRecords(List<Map<String, String>> expectedRecords) {
        assertThat(expectedRecords).hasSize(resultPage.getContent().size());
        for(SearchQuestioningDto searchQuestioningDto : resultPage.getContent()) {
            Optional<Map<String, String>> expectedRecord = expectedRecords
                    .stream()
                    .filter(map -> map.get("id").equals(searchQuestioningDto.getSurveyUnitId()))
                    .findFirst();
            assertThat(expectedRecord).isPresent();
            String[] expectedContactIds = expectedRecord
                    .get()
                    .get("listContacts")
                    .split(",");
            assertThat(searchQuestioningDto.getContactIds()).containsExactlyInAnyOrder(expectedContactIds);
        }
    }


    @Then("the result should contain the following questionings")
    public void theResultShouldContainTheFollowingQuestionings(List<ExpectedQuestioning> expected) {
        List<ExpectedQuestioning> actual = resultPage.getContent().stream()
                .map(dto -> new ExpectedQuestioning(
                        questioningContext.getKey(dto.getQuestioningId()),
                        dto.getSurveyUnitId(),
                        dto.getValidationDate() == null ? null : sdf.format(dto.getValidationDate()),
                        dto.getHighestEventType() == null ? null : dto.getHighestEventType().name(),
                        dto.getLastCommunicationType() == null ? null : dto.getLastCommunicationType().name()
                ))
                .toList();

        assertThat(actual)
                .hasSize(expected.size())
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Then("the total number of results should be {int}")
    public void theTotalNumberOfResultsShouldBe(int totalResults) {
        Assertions.assertEquals(totalResults, resultPage.getNumberOfElements());
    }


    @Then("the result size is {int}")
    public void theResultSizeIs(int size) {
        Assertions.assertEquals(size, resultPage.getNumberOfElements());
    }
}
