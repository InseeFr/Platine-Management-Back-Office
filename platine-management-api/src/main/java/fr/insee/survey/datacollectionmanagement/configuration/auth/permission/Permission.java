package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    READ_AND_WRITE(true);

    private final boolean isGlobalPermission;
    public boolean isGlobalPermission() {
        return isGlobalPermission;
    }
}
