package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Add strategies for each permission
 */
@Configuration
@SuppressWarnings("unchecked")
public class PermissionEvaluatorConfig {
    @Bean
    public Map<Permission, ApplicationPermissionEvaluator<?>> permissionEvaluators(
            List<ApplicationPermissionEvaluator<?>> evaluators) {

        return evaluators.stream()
                .collect(Collectors.toMap(
                        ApplicationPermissionEvaluator::permission,
                        Function.identity()
                ));
    }
}
