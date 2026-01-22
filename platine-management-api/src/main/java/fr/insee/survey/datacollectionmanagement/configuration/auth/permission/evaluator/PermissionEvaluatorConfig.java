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
public class PermissionEvaluatorConfig {
    @Bean
    public <T> Map<Permission, ApplicationPermissionEvaluator<T>> permissionEvaluators(
            List<ApplicationPermissionEvaluator<T>> evaluators) {

        return evaluators.stream()
                .collect(Collectors.toMap(
                        ApplicationPermissionEvaluator::permission,
                        Function.identity()
                ));
    }
}
