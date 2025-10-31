package fr.insee.survey.datacollectionmanagement.user.service;

import fr.insee.survey.datacollectionmanagement.metadata.dto.WalletDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface WalletService {

  /**
   * Parses and performs initial validation on a wallet file.
   * This method validates file format, headers, and field syntax
   * but does NOT check database consistency or in-file business rules.
   *
   * @param file The wallet file (.csv or .json)
   * @return A list of WalletDTOs ready for integration.
   */
  List<WalletDto> parseAndValidateFile(MultipartFile file);
}