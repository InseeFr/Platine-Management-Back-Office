package fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SurveyRepository;
import lombok.Setter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class SurveyRepositoryStub implements SurveyRepository {
    @Setter
    private List<Survey> surveys = new ArrayList<>();

    @Override
    public Page<Survey> findBySourceIdYearPeriodicity(Pageable pageable, String sourceId, Integer year, String periodicity) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Survey> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Survey> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Survey> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Survey getOne(String s) {
        return null;
    }

    @Override
    public Survey getById(String s) {
        return null;
    }

    @Override
    public Survey getReferenceById(String s) {
        return null;
    }

    @Override
    public <S extends Survey> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Survey> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Survey> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Survey> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Survey> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Survey> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Survey, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Survey> S save(S survey) {
        return survey;
    }

    @Override
    public <S extends Survey> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Survey> findById(String identifier) {
        return surveys.stream()
                .filter(survey -> survey.getId().equals(identifier))
                .findFirst();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public List<Survey> findAll() {
        return List.of();
    }

    @Override
    public List<Survey> findAllById(Iterable<String> strings) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void delete(Survey entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends Survey> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Survey> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Survey> findAll(Pageable pageable) {
        return null;
    }
}
