package fr.insee.survey.datacollectionmanagement.user.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class WalletParserFactory {

    private final List<WalletParserStrategy> parsers;

    public WalletParserStrategy getParserForFile(MultipartFile file) {
        String filename = (file == null) ? null : file.getOriginalFilename();
        return findParserForFilename(filename)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unsupported file type. Allowed types are .csv, .json"
                ));
    }

    public Optional<WalletParserStrategy> findParserForFilename(String filename) {
        if (filename == null) return Optional.empty();
        return parsers.stream()
                .filter(p -> p.supports(filename))
                .findFirst();
    }
}
