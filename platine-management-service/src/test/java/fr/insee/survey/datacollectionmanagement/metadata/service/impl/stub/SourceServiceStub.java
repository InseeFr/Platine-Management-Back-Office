package fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.dto.ParamsDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.SourceDto;
import fr.insee.survey.datacollectionmanagement.metadata.service.SourceService;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SourceServiceStub implements SourceService {
    private List<Source> savedSources = new ArrayList<>();

    @Getter
    private Source lastSaved;

    @Override
    public Source findById(String sourceId) {
        return savedSources.stream().filter(source -> source.getId().equals(sourceId))
                .findFirst()
                .get();
    }

    @Override
    public List<Source> findAll() {
        return List.of();
    }

    @Override
    public Source insertOrUpdateSource(Source source) {
        savedSources.removeIf(x -> Objects.equals(x.getId(), source.getId()));
        savedSources.add(source);
        lastSaved = source;
        return source;
    }

    @Override
    public void deleteSourceById(String id) {
        savedSources.removeIf(x -> Objects.equals(x.getId(), id));
    }

    @Override
    public List<ParamsDto> saveParametersForSource(Source source, ParamsDto paramsDto) {
        return List.of();
    }

    @Override
    public List<SourceDto> getOngoingSources() {
        return List.of();
    }

    public void setSavedSources(List<Source> sources) {
        savedSources = new ArrayList<>(sources);
    }
}
