package fr.insee.survey.datacollectionmanagement.integration;

import io.cucumber.java.DataTableType;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.core.options.Constants.PLUGIN_PROPERTY_NAME;

@CucumberContextConfiguration
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("/integration/query")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "fr.insee.survey.datacollectionmanagement.integration")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, json:target/cucumber.json, html:target/cucumber_report.html,"
        + " usage:target/usage.jsonx, junit:target/junit.xml")
public class CucumberTest {
    @DataTableType
    public ExpectedQuestioning toExpectedQuestioning(Map<String, String> row) {
        return new ExpectedQuestioning(
                Integer.parseInt(row.get("id")),
                row.get("surveyUnitId"),
                row.get("validationDate"),
                row.get("highestEventType"),
                row.get("lastCommunicationType")
        );
    }
}
