package fr.insee.survey.datacollectionmanagement.questioning.service.mapper;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitDetailsDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.QuestioningRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.assertj.core.api.Assertions.assertThat;

class SurveyUnitMapperTest {

    private ModelMapper modelMapper;
    private QuestioningRepositoryStub questioningRepositoryStub;
    private SurveyUnitMapper mapper;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        questioningRepositoryStub = new QuestioningRepositoryStub();
        mapper = new SurveyUnitMapper(modelMapper, questioningRepositoryStub);
        mapper.configure();
    }

    @Test
    void givenSurveyUnitWithHasQuestioningsTrue() {
        SurveyUnit su = new SurveyUnit();
        su.setIdSu("SU1");
        su.setIdentificationCode("CODE1");
        su.setIdentificationName("NAME1");
        su.setLabel("entreprise");

        Questioning questioning = new Questioning();
        questioning.setId(1L);
        questioning.setSurveyUnit(su);
        questioningRepositoryStub.save(questioning);

        SurveyUnitDetailsDto dto = mapper.toDto(su);

        assertThat(dto.getIdSu()).isEqualTo("SU1");
        assertThat(dto.getIdentificationCode()).isEqualTo("CODE1");
        assertThat(dto.getIdentificationName()).isEqualTo("NAME1");
        assertThat(dto.getLabel()).isEqualTo("entreprise");
        assertThat(dto.isHasQuestionings()).isTrue();
    }

    @Test
    void givenSurveyUnitWithHasQuestioningsFalse() {
        SurveyUnit su = new SurveyUnit();
        su.setIdSu("SU1");
        su.setIdentificationCode("CODE1");
        su.setIdentificationName("NAME1");
        su.setLabel("entreprise");

        SurveyUnitDetailsDto dto = mapper.toDto(su);

        assertThat(dto.getIdSu()).isEqualTo("SU1");
        assertThat(dto.getIdentificationCode()).isEqualTo("CODE1");
        assertThat(dto.getIdentificationName()).isEqualTo("NAME1");
        assertThat(dto.getLabel()).isEqualTo("entreprise");
        assertThat(dto.isHasQuestionings()).isFalse();
    }


}
