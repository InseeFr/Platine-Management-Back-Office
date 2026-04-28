package fr.insee.survey.datacollectionmanagement.questioning.service;

import fr.insee.survey.datacollectionmanagement.exception.RessourceNotValidatedException;
import fr.insee.survey.datacollectionmanagement.query.domain.ResultUpload;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Upload;
import fr.insee.survey.datacollectionmanagement.questioning.dto.UploadDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public interface UploadService {

    ResultUpload save(String idCampaign, UploadDto uploadDto) throws RessourceNotValidatedException;

    Upload findById(long id);

    List<Upload> findAllByIdCampaign(String idCampaign);

    void delete(Upload up);

    Upload saveAndFlush(Upload up);

    boolean checkUploadDate(String idCampaign, Instant date);

    void removeEmptyUploads();

}
