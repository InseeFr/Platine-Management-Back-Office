package fr.insee.survey.datacollectionmanagement.user.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.survey.datacollectionmanagement.exception.WalletFileProcessingException;
import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JsonWalletParserStrategyTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JsonWalletParserStrategy parser = new JsonWalletParserStrategy(objectMapper);

    @Test
    void supports_shouldReturnTrueForJson_andFalseOtherwise() {
        assertTrue(parser.supports("wallet.json"));
        assertTrue(parser.supports("WALLET.JSON")); // case-insensitive
        assertFalse(parser.supports("wallet.txt"));
        assertFalse(parser.supports(null));
        assertFalse(parser.supports(""));
    }

    @Test
    void parse_shouldReturnDtos_whenJsonIsValid() {
        String json = """
        [
          {"id_su": "su-1", "idep": "user-1", "id_group": "group-a"},
          {"id_su": "su-2", "idep": "user-2", "id_group": "group-b"}
        ]
        """;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "wallet.json",
                "application/json",
                json.getBytes(StandardCharsets.UTF_8)
        );

        List<WalletDto> dtos = parser.parse(file);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());

        WalletDto first = dtos.get(0);

        assertEquals("su-1", first.surveyUnit());
        assertEquals("USER-1", first.internalUser());
        assertEquals("group-a", first.group());

        WalletDto second = dtos.get(1);
        assertEquals("su-2", second.surveyUnit());
        assertEquals("USER-2", second.internalUser());
        assertEquals("group-b", second.group());
    }

    @Test
    void parse_shouldThrow_whenJsonArrayIsEmpty() {
        String json = "[]";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "wallet.json",
                "application/json",
                json.getBytes(StandardCharsets.UTF_8)
        );

        WalletFileProcessingException ex = assertThrows(WalletFileProcessingException.class, () -> parser.parse(file));
        assertNotNull(ex.getCause());
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
        assertTrue(ex.getCause().getMessage().toLowerCase().contains("null or empty"));
    }

    @Test
    void parse_shouldThrow_whenJsonIsMalformed() {
        // JSON malformé volontairement
        String badJson = "{ this is not : valid json ]";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "wallet.json",
                "application/json",
                badJson.getBytes(StandardCharsets.UTF_8)
        );

        WalletFileProcessingException ex = assertThrows(WalletFileProcessingException.class, () -> parser.parse(file));
        assertNotNull(ex.getCause());
        assertInstanceOf(Exception.class, ex.getCause());
        assertTrue(ex.getMessage().toLowerCase().contains("error processing json file"));
    }
}