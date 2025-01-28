package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.RessourceNotValidatedException;
import fr.insee.survey.datacollectionmanagement.query.domain.ResultUpload;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogUploadQuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.UploadDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.UploadRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.UploadService;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.CampaignServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningEventServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningServiceStub;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class UploadServiceImplTest {

    private UploadService uploadService;
    private QuestioningServiceStub questioningServiceStub;
    private QuestioningEventServiceStub questioningEventServiceStub;
    private CampaignServiceStub campaignServiceStub;
    @Mock
    private UploadRepository uploadRepository;

    @BeforeEach
    void init(){
        questioningServiceStub = new QuestioningServiceStub();
        questioningEventServiceStub = new QuestioningEventServiceStub();
        campaignServiceStub = new CampaignServiceStub();
        uploadService = new UploadServiceImpl(uploadRepository, questioningEventServiceStub,campaignServiceStub,questioningServiceStub); ;
    }
    @Test
    @DisplayName("Should save something")
    void testSave() throws RessourceNotValidatedException {
        //Given
        UploadDto uploadDto = new UploadDto();
        MoogUploadQuestioningEventDto moogUploadQuestioningEventDto = new MoogUploadQuestioningEventDto();
        moogUploadQuestioningEventDto.setDate("21012024");
        moogUploadQuestioningEventDto.setStatus(TypeQuestioningEvent.INITLA.name());
        moogUploadQuestioningEventDto.setIdSu("idsu");
        moogUploadQuestioningEventDto.setIdContact("contact");
        uploadDto.setData(List.of(moogUploadQuestioningEventDto));
        
        String campaignId = "EEC2025T01";
        
        //When
        ResultUpload result = uploadService.save(campaignId, uploadDto);

        //Then
        Assertions.assertThat(result).isNotNull();
    }

}