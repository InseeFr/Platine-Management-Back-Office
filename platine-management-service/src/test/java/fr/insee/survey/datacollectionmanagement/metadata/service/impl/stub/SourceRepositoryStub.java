package fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Support;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SourceRepository;
import lombok.Getter;
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

public class SourceRepositoryStub implements SourceRepository {

    @Setter
    @Getter
    private List<Source> sources = new ArrayList<>();

    @Override
    public void flush() {
        // stub
    }

    @Override
    public <S extends Source> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Source> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Source> entities) {
        // stub

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {
        // stub

    }

    @Override
    public void deleteAllInBatch() {
        // stub

    }

    @Override
    public Source getOne(String s) {
        return null;
    }

    @Override
    public Source getById(String s) {
        return null;
    }

    @Override
    public Source getReferenceById(String s) {
        return null;
    }

    @Override
    public <S extends Source> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Source> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Source> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Source> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Source> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Source> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Source, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Source> S save(S entity) {
        sources.removeIf(source -> source.getId().equals(entity.getId()));
        sources.add(entity);
        return entity;
    }

    @Override
    public <S extends Source> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Source> findById(String s) {
        return sources.stream()
                .filter(source -> source.getId().equals(s))
                .findFirst();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public List<Source> findAll() {
        return sources;
    }

    @Override
    public List<Source> findAllById(Iterable<String> strings) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {
        sources.removeIf(source -> source.getId().equals(s));
    }

    @Override
    public void delete(Source entity) {
        // stub

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        // stub

    }

    @Override
    public void deleteAll(Iterable<? extends Source> entities) {
        // stub

    }

    @Override
    public void deleteAll() {
        // stub

    }

    @Override
    public List<Source> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Source> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Optional<Support> findSupportBySourceId(String identifier) {
        Optional<Source> source = findById(identifier);
        return source.map(Source::getSupport);
    }
}
