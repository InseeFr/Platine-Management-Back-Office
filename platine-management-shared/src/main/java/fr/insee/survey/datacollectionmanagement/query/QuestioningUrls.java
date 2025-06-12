package fr.insee.survey.datacollectionmanagement.query;

import java.util.Optional;

public record QuestioningUrls(Optional<String> accessUrl,
                              Optional<String> depositProofUrl,
                              Optional<String> downloadUrl) {

    public static QuestioningUrls forAccess(String accessUrl) {
        return new QuestioningUrls(Optional.ofNullable(accessUrl), Optional.empty(), Optional.empty());
    }

    public static QuestioningUrls forDepositProof(String depositProofUrl) {
        return new QuestioningUrls(Optional.empty(), Optional.ofNullable(depositProofUrl), Optional.empty());
    }

    public static QuestioningUrls forDownload(String downloadUrl) {
        return new QuestioningUrls(Optional.empty(), Optional.empty(), Optional.ofNullable(downloadUrl));
    }

    public static QuestioningUrls empty() {
        return new QuestioningUrls(Optional.empty(), Optional.empty(), Optional.empty());
    }
}

