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
import fr.insee.survey.datacollectionmanagement.questioning.service.impl.QuestioningEventServiceImpl;
import fr.insee.survey.datacollectionmanagement.questioning.service.impl.QuestioningServiceImpl;
import fr.insee.survey.datacollectionmanagement.user.enums.WalletFilterEnum;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchQuestioningSteps {
    @Autowired
    QuestioningServiceImpl questioningService;

    @Autowired
    QuestioningEventServiceImpl questioningEventService;

    @Autowired
    QuestioningRepository questioningRepository;

    @Autowired
    QuestioningEventRepository questioningEventRepository;

    @Autowired
    SurveyUnitRepository surveyUnitRepository;

    @Autowired
    private QuestioningContext questioningContext;

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

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
        questioning.setId(UUID.randomUUID());
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
    UUID realId = questioningContext.getRealId(questioningId);

    Questioning questioning = questioningRepository.getReferenceById(realId);
    QuestioningEvent qe = new QuestioningEvent(date, TypeQuestioningEvent.valueOf(type), questioning);
    questioningEventRepository.save(qe);
    questioning.getQuestioningEvents().add(qe);
    questioningRepository.saveAndFlush(questioning);
    questioningEventService.refreshHighestEvent(realId);
  }


    @Transactional
    @Given("the questioning communication for questioning {int} with type {string} and date {string}")
    public void createQuestioningCommunication(int questioningId, String type, String isoDate) {
        LocalDateTime date = LocalDateTime.parse(isoDate, FORMATTER);
        UUID realId = questioningContext.getRealId(questioningId);
        Questioning questioning = questioningRepository.getReferenceById(realId);
        QuestioningCommunication qc = new QuestioningCommunication(date, TypeCommunicationEvent.valueOf(type), questioning, StatusCommunication.MANUAL);
        questioning.getQuestioningCommunications().add(qc);
        questioningRepository.save(questioning);
    }

    @Transactional
    @Given("the following priorities and scores for questionings")
    public void theFollowingPrioritiesAndScoresForQuestionings(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            Integer key = Integer.valueOf(row.get("id"));
            Long priority = Long.valueOf(row.get("priority"));
            Integer score = Integer.valueOf(row.get("score"));

            UUID realId = questioningContext.getRealId(key);
            Questioning questioning = questioningRepository.getReferenceById(realId);
            questioning.setPriority(priority);
            questioning.setScore(score);
            questioningRepository.save(questioning);
        }
    }

    @When("I search for Questioning with {string} and page {int} with size {int}")
    public void iSearchForQuestioningWithSurveyUnitId(String surveyUnitId, int page, int size) {
        SearchQuestioningParams searchQuestioningParams = new SearchQuestioningParams(surveyUnitId, null, null, null, WalletFilterEnum.ALL);
        resultPage = questioningService.searchQuestionings(
                searchQuestioningParams, PageRequest.of(page, size), null
        );
    }

    @When("I search questionings for campaign {string} and highest event types")
    public void i_search_questionings_for_campaign_and_highest_event_type(String campaignId, List<String> stringTypes) {
        List<TypeQuestioningEvent> types = stringTypes.stream()
                .map(TypeQuestioningEvent::valueOf)
                .toList();
        SearchQuestioningParams searchQuestioningParams = new SearchQuestioningParams(null, List.of(campaignId), types, null, WalletFilterEnum.ALL);
        searchQuestionings(searchQuestioningParams, null);
    }

    @When("I search questionings for campaign {string} and last communication types")
    public void i_search_questionings_for_campaign_and_last_communication_type(String campaignId, List<String> stringTypes) {
        List<TypeCommunicationEvent> types = stringTypes.stream()
                .map(TypeCommunicationEvent::valueOf)
                .toList();
        SearchQuestioningParams searchQuestioningParams = new SearchQuestioningParams(null, List.of(campaignId), null, types, WalletFilterEnum.ALL);
        searchQuestionings(searchQuestioningParams, null);
    }

    @When("I search questionings for campaign {string} and highest event type {string} and last communication type {string}")
    public void i_search_questionings_for_campaign_and_highest_event_type_and_last_communication_type(String campaignId, String highestEventType, String lastCommunicationType) {
        TypeQuestioningEvent highestEventTypeEnum = TypeQuestioningEvent.valueOf(highestEventType);
        TypeCommunicationEvent lastCommunicationTypeEnum = TypeCommunicationEvent.valueOf(lastCommunicationType);
        SearchQuestioningParams searchQuestioningParams = new SearchQuestioningParams(null, List.of(campaignId), List.of(highestEventTypeEnum), List.of(lastCommunicationTypeEnum), WalletFilterEnum.ALL);
        searchQuestionings(searchQuestioningParams, null);
    }

    @When("I search questionings by wallet for user {string}")
    public void iSearchQuestioningsByWalletForUser(String userId) {
        SearchQuestioningParams searchQuestioningParams = new SearchQuestioningParams(null,null,null, null, WalletFilterEnum.MY_WALLET);
        searchQuestionings(searchQuestioningParams, userId);
    }

    @When("I search questionings by groups for user {string}")
    public void iSearchQuestioningsByGroupsForUser(String userId) {
        SearchQuestioningParams searchQuestioningParams = new SearchQuestioningParams(null,null,null, null, WalletFilterEnum.GROUPS);
        searchQuestionings(searchQuestioningParams, userId);
    }

    private void searchQuestionings(SearchQuestioningParams searchQuestioningParams, String userId) {
        resultPage = questioningService.searchQuestionings(
                searchQuestioningParams, PageRequest.of(0, 20), userId);
    }

    @When("I search for all Questioning with page {int} and size {int} sorted by")
    public void iSearchForAllQuestioningWithPageAndSizeSortedBy(int page, int size, DataTable sortTable) {
        List<Map<String, String>> rows = sortTable.asMaps(String.class, String.class);

        List<Sort.Order> orders = rows.stream()
                .map(row -> {
                    String field = row.get("field");
                    String direction = row.get("direction");
                    return new Sort.Order(Sort.Direction.fromString(direction), field);
                })
                .toList();

        SearchQuestioningParams searchQuestioningParams =
                new SearchQuestioningParams(null, null, null, null, WalletFilterEnum.ALL);

        resultPage = questioningService.searchQuestionings(
                searchQuestioningParams,
                PageRequest.of(page, size, Sort.by(orders)),
                null
        );
    }

    @Then("the result should contain the following Questioning related to surveyUnit")
    public void theResultShouldContainTheFollowingQuestioningRecords(List<Map<String, String>> expectedRecords) {
        assertThat(expectedRecords).hasSize(resultPage.getContent().size());
        for(SearchQuestioningDto searchQuestioningDto : resultPage.getContent()) {
            Optional<Map<String, String>> expectedRecord = expectedRecords
                    .stream()
                    .filter(map -> map.get("surveyUnitId").equals(searchQuestioningDto.getSurveyUnitId()))
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
                        dto.getLastCommunication().getTypeCommunicationEvent() == null ? null : dto.getLastCommunication().getTypeCommunicationEvent().name()
                ))
                .toList();

        assertThat(actual)
                .hasSize(expected.size())
                .containsExactlyInAnyOrderElementsOf(expected);
    }

    @Then("the result should contain the following questionings for survey units")
    public void theResultShouldContainTheFollowingQuestioningsForSurveyUnits(List<Map<String, String>> expectedRecords) {
            assertThat(expectedRecords).hasSize(resultPage.getContent().size());
            for(SearchQuestioningDto searchQuestioningDto : resultPage.getContent()) {
                Optional<Map<String, String>> expectedRecord = expectedRecords
                        .stream()
                        .filter(map -> map.get("surveyUnitId").equals(searchQuestioningDto.getSurveyUnitId()))
                        .findFirst();
                assertThat(expectedRecord).isPresent();
            }
    }

    @Then("the total number of results should be {int}")
    public void theTotalNumberOfResultsShouldBe(int totalResults) {
        Assertions.assertEquals(totalResults, resultPage.getNumberOfElements());
    }


    @Then("the result size is {int}")
    public void theResultSizeIs(int size) {
        Assertions.assertEquals(size, resultPage.getNumberOfElements());
    }


    @Then("the result should contain questionings in the following order")
    public void theResultShouldContainTheFollowingQuestioningsInOrder(List<Map<String, String>> expectedRecords) {
        List<Integer> expectedIds = expectedRecords.stream()
                .map(m -> Integer.valueOf(m.get("id")))
                .toList();

        List<Integer> actualIds = resultPage.getContent().stream()
                .map(dto -> questioningContext.getKey(dto.getQuestioningId()))
                .toList();

        assertThat(actualIds)
                .containsExactlyElementsOf(expectedIds);
    }

}
