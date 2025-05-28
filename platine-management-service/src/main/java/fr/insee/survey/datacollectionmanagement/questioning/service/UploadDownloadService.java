package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.exception.UploadDownloadException;

public interface UploadDownloadService {

    String uploadFile();

    String downloadFile() throws UploadDownloadException;

}
