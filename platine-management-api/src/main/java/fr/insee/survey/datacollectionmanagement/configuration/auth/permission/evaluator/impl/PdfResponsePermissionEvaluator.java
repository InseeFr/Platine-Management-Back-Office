package fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.impl;

import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.Permission;
import fr.insee.survey.datacollectionmanagement.configuration.auth.permission.evaluator.ApplicationPermissionEvaluator;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@RequiredArgsConstructor
@Component
public class PdfResponsePermissionEvaluator implements ApplicationPermissionEvaluator<UUID> {

    private final GlobalPermissionChecker globalPermissionChecker;
    private final QuestioningService questioningService;

    @Override
    public Permission permission() {
        return Permission.READ_PDF_RESPONSE;
    }

    @Override
    public Class<UUID> targetType() {
        return UUID.class;
    }

    @Override
    public boolean hasPermission(Authentication authentication, UUID questioningId) {

        boolean hasGlobalPermission = globalPermissionChecker.hasPermission(authentication, this.permission());
        if (!hasGlobalPermission) {
            return false;
        }
        return questioningService.isQuestioningInBusinessSource(questioningId);
    }
}
