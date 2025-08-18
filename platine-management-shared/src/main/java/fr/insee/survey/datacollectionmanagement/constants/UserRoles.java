package fr.insee.survey.datacollectionmanagement.constants;

public class UserRoles {
    private UserRoles() {
        throw new IllegalStateException("Utility class");
    }

    public static final String INTERVIEWER = "interviewer";
    public static final String REVIEWER = "reviewer";
    public static final String EXPERT = "EXPERT";
}