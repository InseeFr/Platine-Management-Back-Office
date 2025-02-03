package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Parameters;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.dto.ParamsDto;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.ParametersService;
import fr.insee.survey.datacollectionmanagement.metadata.service.SourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {

    private final SourceRepository sourceRepository;

    private final ParametersService parametersService;

    public Source findById(String source) {
        return sourceRepository.findById(source)
                .orElseThrow(() -> new NotFoundException(String.format("Source %s not found", source)));
    }

    @Override
    public List<Source> findAll() {
        return sourceRepository.findAll();
    }

    @Override
    public Source insertOrUpdateSource(Source source) {
        try {
            Source sourceBase = findById(source.getId());
            log.info("Update source with the id {}", source.getId());
            source.setSurveys(sourceBase.getSurveys());
        } catch (NotFoundException e) {
            log.info("Create source with the id {}", source.getId());
            return sourceRepository.save(source);

        }
        return sourceRepository.save(source);
    }

    @Override
    public void deleteSourceById(String id) {
        sourceRepository.deleteById(id);

    }

    @Override
    public List<ParamsDto> saveParametersForSource(Source source, ParamsDto paramsDto) {
        Parameters param = parametersService.convertToEntity(paramsDto);
        param.setMetadataId(StringUtils.upperCase(source.getId()));
        Set<Parameters> updatedParams = parametersService.updateSourceParams(source,param);
        source.setParams(updatedParams);
        insertOrUpdateSource(source);
        return updatedParams.stream().map(parametersService::convertToDto).toList();
    }

}
