package fr.insee.survey.datacollectionmanagement.contact.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;


public class PayloadUtil {

    private PayloadUtil() {
        throw new IllegalStateException("Utility class");
    }


    public static JsonNode getPayloadAuthor(String author) {
        Map<String, String> mapAuthor = new HashMap<>();
        mapAuthor.put("author", author);
        return new ObjectMapper().valueToTree(mapAuthor);
    }


}
