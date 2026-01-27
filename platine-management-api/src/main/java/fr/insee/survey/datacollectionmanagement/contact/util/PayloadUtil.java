package fr.insee.survey.datacollectionmanagement.contact.util;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.Map;


public class PayloadUtil {

    private PayloadUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static JsonNode getPayloadAuthorAndSource(String author, String source) {
      Map<String, String> mapAuthorAndSource = new HashMap<>();
      mapAuthorAndSource.put("author", author);
      mapAuthorAndSource.put("source", source);
      return new JsonMapper().valueToTree(mapAuthorAndSource);
    }


}
