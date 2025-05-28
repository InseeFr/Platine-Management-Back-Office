package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.UploadDownloadException;
import fr.insee.survey.datacollectionmanagement.questioning.service.UploadDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;

@RequiredArgsConstructor
public class UploadDownloadServiceHttpImpl implements UploadDownloadService {

    private final RestTemplate uploadDownloadRestTemplate;

    @Override
    public String uploadFile() {
        return "";
    }


    @Override
    public String downloadFile() throws UploadDownloadException {
        final String uriUploadDownload = "https://r2d2.fr";

        try {
            ResponseEntity<InputStream> response =
                    uploadDownloadRestTemplate.exchange(uriUploadDownload, HttpMethod.GET,
                            HttpEntity.EMPTY,
                            InputStream.class);
            InputStream fileInputStream = response.getBody();
            return fileInputStream.toString();

        } catch (RestClientException ex) {
            throw new UploadDownloadException(ex.getMessage());
        }

    }

}