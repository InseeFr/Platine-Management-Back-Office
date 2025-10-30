package fr.insee.survey.datacollectionmanagement.user.service;

import org.springframework.web.multipart.MultipartFile;

public interface WalletService {

  /**
   * Import and process a file (CSV or JSON) to create or update wallets for a given source.
   * @param sourceId the source identifier
   * @param file the CSV or JSON file
   */
  void importWallets(String sourceId, MultipartFile file);
}
