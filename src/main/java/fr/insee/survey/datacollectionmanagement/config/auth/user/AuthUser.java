package fr.insee.survey.datacollectionmanagement.config.auth.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class AuthUser {

    private final String id;

    private final List<String> roles;


}
