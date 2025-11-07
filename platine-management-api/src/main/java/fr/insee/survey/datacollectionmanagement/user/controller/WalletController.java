package fr.insee.survey.datacollectionmanagement.user.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.exception.WalletBusinessRuleException;
import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.user.service.WalletService;
import fr.insee.survey.datacollectionmanagement.user.validation.ValidationWalletError;
import fr.insee.survey.datacollectionmanagement.user.validator.WalletValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final WalletValidator walletValidator;

    @Operation(summary = "Create or update wallets from file (CSV or JSON)")
    @PostMapping(
            value = UrlConstants.API_SOURCE_ID_WALLET,
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File processed successfully",
                    content = @Content(schema = @Schema(example = "{\"message\": \"File processed successfully\"}"))),
            @ApiResponse(responseCode = "400", description = "Invalid file or business rule violation",
                    content = @Content(schema = @Schema(example = "{\"error\": \"Invalid file or data\"}"))),
            @ApiResponse(responseCode = "500", description = "Unexpected error",
                    content = @Content(schema = @Schema(example = "{\"error\": \"An unexpected error occurred while processing the file.\"}")))
    })
    public ResponseEntity<Map<String, String>> importWallets(
            @PathVariable("id") String source,
            @RequestParam("file") MultipartFile file) {

        log.info("Importing wallets for sourceId {} from file {}", source, file.getOriginalFilename());
        List<WalletDto> wallets = walletService.parse(file);

        log.info("Validating wallets for sourceId {}", source);
        List<ValidationWalletError> validationErrors = walletValidator.validate(wallets);
        if (!validationErrors.isEmpty()) {
            List<String> errorMessages = validationErrors.stream()
                    .map(ValidationWalletError::toString)
                    .toList();
            errorMessages.forEach(log::error);
            throw new WalletBusinessRuleException("Invalid Data", errorMessages);
        }

        walletService.integrateWallets(source, wallets);

        return ResponseEntity.ok(Map.of("message", "File processed successfully"));
    }

}
