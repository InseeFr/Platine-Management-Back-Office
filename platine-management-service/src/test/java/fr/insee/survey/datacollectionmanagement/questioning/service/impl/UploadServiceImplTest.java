package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.RessourceNotValidatedException;
import fr.insee.survey.datacollectionmanagement.query.domain.ResultUpload;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogUploadQuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.UploadDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.UploadRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.CampaignServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningEventServiceStub;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningServiceStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UploadServiceImplTest {

    private UploadServiceImpl uploadService;
    QuestioningEventServiceStub questioningEventServiceStub;
    QuestioningServiceStub questioningServiceStub;

    @Mock
    private UploadRepository uploadRepository;

    @BeforeEach
    void init(){
        questioningServiceStub = new QuestioningServiceStub();
        questioningEventServiceStub = new QuestioningEventServiceStub();
        CampaignServiceStub campaignServiceStub = new CampaignServiceStub();
        uploadService = new UploadServiceImpl(uploadRepository, questioningEventServiceStub,null, campaignServiceStub, questioningServiceStub);
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
        when(uploadRepository.saveAndFlush(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //Then
        ResultUpload result = uploadService.save(campaignId, uploadDto);
        assertThat(result).isNotNull();

        assertThat(result.getListIdOK())
                .hasSize(1);

        assertThat(result.getListIdKO())
                .isEmpty();
    }

    @Test
    @DisplayName("Should rename VALPAP to RECUPAP")
    void test_should_rename_valpap_to_recupap() {
        MoogUploadQuestioningEventDto moogUploadValpap = new MoogUploadQuestioningEventDto();
        moogUploadValpap.setStatus("VALPAP");
        moogUploadValpap = uploadService.renameValpaptoRecupap(moogUploadValpap);
        assertThat(moogUploadValpap.getStatus()).isEqualTo("RECUPAP");

        MoogUploadQuestioningEventDto moogUploaRecupap = new MoogUploadQuestioningEventDto();
        moogUploaRecupap.setStatus("RECUPAP");
        moogUploaRecupap = uploadService.renameValpaptoRecupap(moogUploaRecupap);
        assertThat(moogUploaRecupap.getStatus()).isEqualTo("RECUPAP");


    }
}