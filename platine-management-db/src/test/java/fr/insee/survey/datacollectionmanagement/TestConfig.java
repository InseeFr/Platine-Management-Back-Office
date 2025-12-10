package fr.insee.survey.datacollectionmanagement;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan(basePackages = {
        "fr.insee.survey.datacollectionmanagement.user.dao",
})
@EntityScan(basePackages = {
        "fr.insee.survey.datacollectionmanagement.user.domain",
        "fr.insee.survey.datacollectionmanagement.metadata.domain",
        "fr.insee.survey.datacollectionmanagement.questioning.domain",
})
@EnableJpaRepositories(basePackages = {
        "fr.insee.survey.datacollectionmanagement.user.repository",
        "fr.insee.survey.datacollectionmanagement.metadata.repository",
        "fr.insee.survey.datacollectionmanagement.questioning.repository",
})
public class TestConfig {
}
