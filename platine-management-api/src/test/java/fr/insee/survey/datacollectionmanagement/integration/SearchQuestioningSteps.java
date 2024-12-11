package fr.insee.survey.datacollectionmanagement.integration;

import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.impl.QuestioningServiceImpl;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchQuestioningSteps {
    @Autowired
    QuestioningServiceImpl questioningService;

    private Page<SearchQuestioningDto> resultPage;


    @When("I search for Questioning with {string} and page {int} with size {int}")
    public void iSearchForQuestioningWithSurveyUnitId(String surveyUnitId, int page, int size) {
        resultPage = questioningService.searchQuestioning(
                surveyUnitId, PageRequest.of(page, size)
        );
    }

    @When("I search for all Questioning with page {int} and size {int}")
    public void iSearchForAllQuestioningWithPageAndSize(int page, int size) {
        resultPage = questioningService.searchQuestioning(
                "", PageRequest.of(page, size)
        );
    }

    @Then("the result should contain the following Questioning related to surveyUnit:")
    public void theResultShouldContainTheFollowingQuestioningRecords(List<Map<String, String>> expectedRecords) {
        List<String> expectedIds = expectedRecords.stream()
                .map(row -> row.get("id")) //id from faeture table
                .toList();
        List<List<String>> expectedContacts = expectedRecords.stream()
                .map(row -> Arrays.stream(row.get("listContacts").split(",")).toList()) //id from faeture table
                .toList();
        List<String> actualIds = resultPage.getContent().stream()
                .map(SearchQuestioningDto::getSurveyUnitId)
                .toList();
        List<List<String>> actualContactsIds = resultPage.getContent().stream()
                .map(SearchQuestioningDto::getListContactIdentifiers)
                .toList();
        assertThat(expectedIds).containsExactlyInAnyOrderElementsOf(actualIds);
        assertThat(expectedContacts).hasSameSizeAs(actualContactsIds);

        for (int i = 0; i < expectedContacts.size(); i++) {
            assertThat(expectedContacts.get(i)).containsExactlyInAnyOrderElementsOf(actualContactsIds.get(i));

        }


    }

    @Then("the total number of results should be {int}")
    public void theTotalNumberOfResultsShouldBe(int totalResults) {
        Assertions.assertEquals(totalResults, resultPage.getTotalElements());
    }


    @Then("the result size is {int}")
    public void theResultSizeIs(int size) {
        Assertions.assertEquals(size, resultPage.getTotalElements());
    }
}
