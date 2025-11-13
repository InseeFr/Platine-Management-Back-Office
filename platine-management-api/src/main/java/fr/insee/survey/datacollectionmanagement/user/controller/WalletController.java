package fr.insee.survey.datacollectionmanagement.user.controller;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthorityPrivileges;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.exception.WalletBusinessRuleException;
import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;
import fr.insee.survey.datacollectionmanagement.user.service.WalletService;
import fr.insee.survey.datacollectionmanagement.user.service.WalletValidationService;
import fr.insee.survey.datacollectionmanagement.user.utils.WalletParserFactory;
import fr.insee.survey.datacollectionmanagement.user.utils.WalletParserStrategy;
import fr.insee.survey.datacollectionmanagement.user.validation.ValidationWalletError;
import fr.insee.survey.datacollectionmanagement.user.validator.WalletValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

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
    private final WalletParserFactory parserFactory;

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
    public void importWallets(
            @PathVariable("id") String source,
            @RequestParam("file") MultipartFile file) {

        String filename = file.getOriginalFilename();
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename is missing");
        }
        log.info("Parsing wallets for source {} from file {}", source, filename);
        WalletParserStrategy parserStrategy = parserFactory.getParserForFile(file);
        List<WalletDto> wallets = parserStrategy.parse(file);

        log.info("Validating inputs wallets for source {}", source);
        List<ValidationWalletError> validationErrors = walletValidator.getWalletInputErrors(wallets);
        if (!validationErrors.isEmpty()) {
            List<String> errorMessages = validationErrors.stream()
                    .map(ValidationWalletError::toString)
                    .toList();
            errorMessages.forEach(log::error);
            throw new WalletBusinessRuleException("Invalid Data", errorMessages);
        }

        log.info("Integrate data for source {}", source);
        walletService.integrateWallets(source, wallets);
        log.info("Successfully integrated wallets for source {}", source);
    }

}
