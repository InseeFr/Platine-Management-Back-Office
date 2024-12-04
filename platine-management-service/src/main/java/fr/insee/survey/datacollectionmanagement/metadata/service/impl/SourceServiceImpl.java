package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.SourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {

    private final SourceRepository sourceRepository;

    public Source findById(String source) {
        return sourceRepository.findById(source).orElseThrow(() -> new NotFoundException(String.format("Source %s not found", source)));
    }

    @Override
    public Page<Source> findAll(Pageable pageable) {
        return sourceRepository.findAll(pageable);
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

}
