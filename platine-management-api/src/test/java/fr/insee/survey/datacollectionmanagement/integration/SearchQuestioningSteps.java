package fr.insee.survey.datacollectionmanagement.integration;

import fr.insee.survey.datacollectionmanagement.query.dto.SearchQuestioningDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.impl.QuestioningServiceImpl;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            assertThat(searchQuestioningDto.getListContactIdentifiers()).containsExactlyInAnyOrder(expectedContactIds);
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
