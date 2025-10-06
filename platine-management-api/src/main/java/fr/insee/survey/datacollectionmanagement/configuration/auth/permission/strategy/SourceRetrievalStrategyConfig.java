package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.strategy;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Add strategies for each permission
 */
@Configuration
public class SourceRetrievalStrategyConfig {
    @Bean
    public Map<Permission, SourceRetrievalStrategy> sourcesStrategies(
            RetrieveSourceFromInterrogationStrategy fromInterrogationStrategy,
            RetrieveSourceFromPartitionStrategy fromPartitionStrategy) {

        return Map.of(Permission.READ_SUPPORT, fromInterrogationStrategy);
    }
}
