package fr.insee.survey.datacollectionmanagement.integration;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@ScenarioScope
public class QuestioningContext {
    private final Map<Integer, UUID> questioningIdMap = new HashMap<>();

    public void registerQuestioning(Integer key, UUID realId) {
        questioningIdMap.put(key, realId);
    }

    public UUID getRealId(Integer key) {
        UUID id = questioningIdMap.get(key);
        if (id == null) {
            throw new IllegalStateException("No questioning registered for key " + key);
        }
        return id;
    }

    public Integer getKey(UUID realId) {
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
