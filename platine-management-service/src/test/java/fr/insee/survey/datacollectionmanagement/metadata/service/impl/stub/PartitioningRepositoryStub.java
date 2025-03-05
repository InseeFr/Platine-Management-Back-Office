package fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.repository.PartitioningRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class PartitioningRepositoryStub implements PartitioningRepository {
    @Override
    public Partitioning findRandomPartitioning() {
        return null;
    }

    @Override
    public List<String> findIdPartitioningBySourceIdYearPeriod(String sourceId, String year, String period) {
        return List.of();
    }

    @Override
    public List<String> findIdPartitioningBySourceId(String sourceId) {
        return List.of();
    }

    @Override
    public List<String> findIdPartitioningByYear(String year) {
        return List.of();
    }

    @Override
    public List<String> findIdPartitioningByPeriod(String period) {
        return List.of();
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Partitioning> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Partitioning> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Partitioning> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Partitioning getOne(String s) {
        return null;
    }

    @Override
    public Partitioning getById(String s) {
        return null;
    }

    @Override
    public Partitioning getReferenceById(String s) {
        return null;
    }

    @Override
    public <S extends Partitioning> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Partitioning> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Partitioning> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Partitioning> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Partitioning> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Partitioning> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Partitioning, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Partitioning> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Partitioning> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Partitioning> findById(String s) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public List<Partitioning> findAll() {
        return List.of();
    }

    @Override
    public List<Partitioning> findAllById(Iterable<String> strings) {
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
    public void delete(Partitioning entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends Partitioning> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Partitioning> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Partitioning> findAll(Pageable pageable) {
        return null;
    }
}
