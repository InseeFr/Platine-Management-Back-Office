package fr.insee.survey.datacollectionmanagement.user.service;

import fr.insee.survey.datacollectionmanagement.exception.WalletFileProcessingException;
import fr.insee.survey.datacollectionmanagement.metadata.dto.WalletDto;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Parsing strategy for wallet files in CSV format.
 */
@Component
public class CsvWalletParserStrategy implements
    WalletParserStrategy {

  private static final String HEADER_SURVEY_UNIT = "surveyUnit";
  private static final String HEADER_INTERNAL_USER = "internal_user";
  private static final String HEADER_GROUP = "group";
  private static final String[] REQUIRED_HEADERS = {
      HEADER_SURVEY_UNIT,
      HEADER_INTERNAL_USER,
      HEADER_GROUP
  };

  /**
   * Checks if this parser supports the given file (by extension).
   *
   * @param filename The name of the file.
   * @return true if filename ends with ".csv", false otherwise.
   */
  @Override
  public boolean supports(String filename) {
    if (filename == null) {
      return false;
    }
    return filename.toLowerCase().endsWith(".csv");
  }

  /**
   * Parses the CSV file into a list of WalletDTOs.
   *
   * @param file The CSV file to parse.
   * @return A list of {@link WalletDto}.
   */
  @Override
  public List<WalletDto> parse(MultipartFile file) {
    List<WalletDto> dtos = new ArrayList<>();
    try (
        InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
        CSVParser csvParser = CSVFormat.DEFAULT.builder()
            .setDelimiter(',')
            .setHeader(REQUIRED_HEADERS)
            .setSkipHeaderRecord(true)
            .setTrim(true)
            .get()
            .parse(reader)
    ) {
      validateHeader(csvParser.getHeaderNames());

      for (CSVRecord csvRecord : csvParser) {
        WalletDto dto = new WalletDto(
            csvRecord.get(HEADER_SURVEY_UNIT),
            csvRecord.get(HEADER_INTERNAL_USER),
            csvRecord.get(HEADER_GROUP)
        );
        dtos.add(dto);
      }

      if (dtos.isEmpty()) {
        throw new IllegalArgumentException("CSV is empty (no data rows)");
      }
      return dtos;

    } catch (Exception e) {
      throw new WalletFileProcessingException("Error processing CSV file: " + e.getMessage(), e);
    }
  }

  /**
   * Validates that the CSV file contains all required headers.
   */
  private void validateHeader(List<String> headerNames) {
    if (headerNames == null || headerNames.isEmpty()) {
      throw new IllegalArgumentException("CSV header is missing or empty.");
    }
    Set<String> headerNameSet = new HashSet<>(headerNames);
    if (!headerNameSet.containsAll(List.of(REQUIRED_HEADERS))) {
      throw new IllegalArgumentException("CSV header must contain: "
          + HEADER_SURVEY_UNIT + ", "
          + HEADER_INTERNAL_USER + ", "
          + HEADER_GROUP);
    }
  }
}