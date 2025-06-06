package fr.insee.survey.datacollectionmanagement.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonUtil {
    public static JsonNode createPayload(String sourceLabel) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();
        node.put("source", sourceLabel);
        return node;
    }
}
