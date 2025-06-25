package fr.insee.survey.datacollectionmanagement.integration;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ScenarioScope
public class QuestioningContext {
    private final Map<Integer, Long> questioningIdMap = new HashMap<>();

    public void registerQuestioning(Integer key, Long realId) {
        questioningIdMap.put(key, realId);
    }

    public Long getRealId(Integer key) {
        Long id = questioningIdMap.get(key);
        if (id == null) {
            throw new IllegalStateException("No questioning registered for key " + key);
        }
        return id;
    }

    public Integer getKey(Long realId) {
        return questioningIdMap.entrySet()
                .stream()
                .filter(e -> e.getValue().equals(realId))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException("No questioning key registered for realId " + realId)
                );
    }
}
