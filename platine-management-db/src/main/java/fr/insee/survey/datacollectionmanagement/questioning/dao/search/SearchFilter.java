package fr.insee.survey.datacollectionmanagement.questioning.dao.search;

import java.util.Map;

public record SearchFilter(
        String sqlFilter,
        Map<String, Object> parameters
) {
}
