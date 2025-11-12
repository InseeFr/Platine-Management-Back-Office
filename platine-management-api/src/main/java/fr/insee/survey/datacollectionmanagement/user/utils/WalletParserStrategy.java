package fr.insee.survey.datacollectionmanagement.user.utils;

import fr.insee.survey.datacollectionmanagement.user.dto.WalletDto;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * Strategy pattern interface defining a wallet file parser.
 */
public interface WalletParserStrategy {


    /**
     * Checks if this parser can handle the given file.
     *
     * @param filename The name of the file.
     * @return true if supported, false otherwise.
     */
    boolean supports(String filename);

    /**
     * Parses the provided file and transforms it into a list of DTOs.
     *
     * @param file The file to parse.
     * @return A list of {@link WalletDto}.
     * @throws fr.insee.survey.datacollectionmanagement.exception.WalletFileProcessingException for file processing errors.
     * @throws IllegalArgumentException                                                         for format issues (e.g., missing header, empty file).
     */
    List<WalletDto> parse(MultipartFile file);
}