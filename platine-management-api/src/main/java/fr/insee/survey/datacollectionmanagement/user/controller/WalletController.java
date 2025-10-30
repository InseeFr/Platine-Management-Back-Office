package fr.insee.survey.datacollectionmanagement.user.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.user.service.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@PreAuthorize(AuthorityPrivileges.HAS_ADMIN_PRIVILEGES)
@Tag(name = "7 - User", description = "Endpoints to create or update wallets from file")
@Slf4j
@Validated
@RequiredArgsConstructor
public class WalletController {

  private final WalletService walletService;

  @Operation(summary = "Create or update wallets from file (CSV or JSON)")
  @PostMapping(
      value = UrlConstants.API_SOURCE_ID_WALLET,
      consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "File processed successfully"),
      @ApiResponse(responseCode = "400", description = "Invalid file or business rule violation"),
      @ApiResponse(responseCode = "500", description = "Unexpected error")
  })
  public ResponseEntity<String> importWallets(
      @PathVariable("id")  String source,
      @RequestParam("file") MultipartFile file) {

    log.info("Importing wallets for sourceId {} from file {}", source, file.getOriginalFilename());

    try {
      walletService.importWallets(source, file);
      return ResponseEntity.ok("File processed successfully");
    } catch (IllegalArgumentException e) {
      log.warn("Invalid file or data: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      log.error("Unexpected error while processing wallet file", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred while processing the file.");
    }
  }
}
