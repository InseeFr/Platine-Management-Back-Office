package fr.insee.survey.datacollectionmanagement.user.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.survey.datacollectionmanagement.exception.WalletFileProcessingException;
import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Parsing strategy for wallet files in JSON format.
 */
@Component
@RequiredArgsConstructor
public class JsonWalletParserStrategy implements
    WalletParserStrategy {

  private final ObjectMapper objectMapper;

  /**
   * Checks if this parser supports the given file (by extension).
   *
   * @param filename The name of the file.
   * @return true if filename ends with ".json", false otherwise.
   */
  @Override
  public boolean supports(String filename) {
    if (filename == null) {
      return false;
    }
    return filename.toLowerCase().endsWith(".json");
  }

  /**
   * Parses the JSON file into a list of WalletDTOs.
   *
   * @param file The JSON file to parse.
   * @return A list of {@link WalletDto}.
   */
  @Override
  public List<WalletDto> parse(MultipartFile file) {
    try {
      List<WalletDto> dtos = objectMapper.readValue(
          file.getInputStream(),
          new TypeReference<List<WalletDto>>() {}
      );

      if (dtos == null || dtos.isEmpty()) {
        throw new IllegalArgumentException("JSON array is null or empty");
      }

      return dtos;

    } catch (Exception e) {
      // Catch JSON parsing exceptions or IO errors
      throw new WalletFileProcessingException("Error processing JSON file: " + e.getMessage(), e);
    }
  }
}