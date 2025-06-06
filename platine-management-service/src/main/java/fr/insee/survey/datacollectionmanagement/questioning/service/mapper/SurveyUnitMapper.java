package fr.insee.survey.datacollectionmanagement.questioning.service.mapper;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitDetailsDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SurveyUnitMapper {

    private final ModelMapper modelMapper;
    private final QuestioningRepository repository;

    @PostConstruct
    void configure() {

        TypeMap<SurveyUnit, SurveyUnitDetailsDto> typeMap =
                modelMapper.createTypeMap(SurveyUnit.class, SurveyUnitDetailsDto.class);

        typeMap.addMappings(m -> m.skip(SurveyUnitDetailsDto::setHasQuestionings));

        typeMap.setPostConverter(ctx -> {
            SurveyUnit source = ctx.getSource();
            SurveyUnitDetailsDto dest = ctx.getDestination();

            boolean hasQs = repository.existsBySurveyUnitIdSu(source.getIdSu());
            dest.setHasQuestionings(hasQs);

            return dest;
        });
    }

    public SurveyUnitDetailsDto toDto(SurveyUnit entity) {
        return modelMapper.map(entity, SurveyUnitDetailsDto.class);
    }
}
