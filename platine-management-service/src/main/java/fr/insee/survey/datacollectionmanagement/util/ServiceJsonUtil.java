package fr.insee.survey.datacollectionmanagement.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public class ServiceJsonUtil {
    public static JsonNode createPayload(String sourceLabel) {
        JsonMapper mapper = new JsonMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("source", sourceLabel);
        return node;
    }
}
