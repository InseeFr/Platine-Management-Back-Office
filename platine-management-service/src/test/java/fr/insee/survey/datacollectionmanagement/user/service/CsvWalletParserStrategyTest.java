package fr.insee.survey.datacollectionmanagement.user.service;

import fr.insee.survey.datacollectionmanagement.exception.WalletFileProcessingException;
import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CsvWalletParserStrategyTest {

    private final CsvWalletParserStrategy parser = new CsvWalletParserStrategy();


    @Test
    void supports_shouldReturnTrueForCsv_andFalseOtherwise() {
        assertTrue(parser.supports("wallet.csv"));
        assertTrue(parser.supports("WALLET.CSV"));
        assertFalse(parser.supports("wallet.txt"));
        assertFalse(parser.supports(null));
        assertFalse(parser.supports(""));
    }

    @Test
    void parse_shouldReturnDtos_whenCsvIsValid() {
        String csv = """
                 surveyUnit,internal_user,group
                 su-1,user-1,group-a
                 su-2,user-2,group-b
                
                """;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "wallet.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8)
        );

        List<WalletDto> dtos = parser.parse(file);

        assertNotNull(dtos);
        assertEquals(2, dtos.size());

        WalletDto first = dtos.getFirst();
        assertEquals("su-1", first.surveyUnit());
        assertEquals("user-1", first.internalUser());
        assertEquals("group-a", first.group());

        WalletDto second = dtos.get(1);
        assertEquals("su-2", second.surveyUnit());
        assertEquals("user-2", second.internalUser());
        assertEquals("group-b", second.group());
    }

    @Test
    void parse_shouldThrow_whenHeaderIsMissing() {

        String csv = """
                        surveyUnit,internal_user
                        su-1,user-1
                     """;
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "wallet.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8)
        );

        WalletFileProcessingException ex = assertThrows(WalletFileProcessingException.class, () -> parser.parse(file));
        assertTrue(ex.getMessage().toLowerCase().contains("error processing csv file"));
        assertNotNull(ex.getCause());
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
    }

    @Test
    void parse_shouldThrow_whenNoDataRows() {

        String csv = "surveyUnit,internal_user,group";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "wallet.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8)
        );

        WalletFileProcessingException ex = assertThrows(WalletFileProcessingException.class, () -> parser.parse(file));
        assertNotNull(ex.getCause());
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
        assertTrue(ex.getCause().getMessage().toLowerCase().contains("csv is empty"));
    }

    @Test
    void parse_shouldWrapAnyExceptionInWalletFileProcessingException() {
        String badContent = "�\u0000\u0001";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "wallet.csv",
                "text/csv",
                badContent.getBytes(StandardCharsets.UTF_8)
        );

        assertThrows(WalletFileProcessingException.class, () -> parser.parse(file));
    }

    @Test
    void parse_shouldThrow_whenHeaderIsNullOrEmpty() {

        String csv = "";
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "wallet.csv",
                "text/csv",
                csv.getBytes(StandardCharsets.UTF_8)
        );

        var ex = assertThrows(RuntimeException.class, () -> parser.parse(file));

        assertNotNull(ex.getCause());
        assertInstanceOf(IllegalArgumentException.class, ex.getCause());
    }
}