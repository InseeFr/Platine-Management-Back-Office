package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {
    READ_INTERRO(false),
    UPDATE_INTERRO(false),
    DELETE_INTERRO(false),
    READ_PARTITION(false),
    UPDATE_PARTITION(false),
    DELETE_PARTITION(false),
    READ_ASSISTANCE(true);

    private final boolean isGlobalPermission;

    public boolean isGlobalPermission() {
        return isGlobalPermission;
    }
}
