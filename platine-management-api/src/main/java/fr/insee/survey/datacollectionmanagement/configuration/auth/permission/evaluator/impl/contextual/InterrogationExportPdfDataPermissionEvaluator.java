package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.contextual;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.ApplicationPermissionEvaluator;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl.GlobalPermissionChecker;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class InterrogationExportPdfDataPermissionEvaluator implements ApplicationPermissionEvaluator<UUID> {

    private final GlobalPermissionChecker globalRoleChecker;
    private final QuestioningService questioningService;

    @Override
    public Permission permission() {
        return Permission.INTERROGATION_EXPORT_PDF_DATA;
    }

    @Override
    public Class<UUID> targetType() {
        return UUID.class;
    }

    @Override
    public boolean hasPermission(Authentication authentication, UUID questioningId) {
        boolean hasValidRole = globalRoleChecker.hasPermission(authentication, this.permission());
        if (!hasValidRole) {
            return false;
        }
        return questioningService.canExportQuestioningDataToPdf(questioningId);
    }
}
