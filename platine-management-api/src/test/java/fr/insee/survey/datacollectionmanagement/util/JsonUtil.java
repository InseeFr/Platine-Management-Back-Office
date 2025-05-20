package fr.insee.survey.datacollectionmanagement.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;

public class JsonUtil {


    public static String createJsonError(int code, String message) {
        JSONObject jo = new JSONObject();
        jo.put("code", code);
        jo.put("message", message);
        return jo.toString();
    }

    public static String createJsonErrorBadRequest(String message) {
        return createJsonError(HttpStatus.BAD_REQUEST.value(), message);
    }
}
