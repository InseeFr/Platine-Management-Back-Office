package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.repository.CampaignRepository;
import lombok.Setter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CampaignRepositoryStub implements CampaignRepository {

    @Setter
    private List<Campaign> campaigns;

    @Override
    public List<Campaign> findByPeriod(String period) {
        return List.of();
    }

    @Override
    public List<Campaign> findBySourceYearPeriod(String source, Integer year, String period) {
        return List.of();
    }

    @Override
    public List<Campaign> findBySourcePeriod(String source, String period) {
        return List.of();
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Campaign> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Campaign> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Campaign> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Campaign getOne(String s) {
        return null;
    }

    @Override
    public Campaign getById(String s) {
        return null;
    }

    @Override
    public Campaign getReferenceById(String s) {
        return null;
    }

    @Override
    public <S extends Campaign> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Campaign> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Campaign> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Campaign> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Campaign> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Campaign> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Campaign, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Campaign> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Campaign> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Campaign> findById(String s) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public List<Campaign> findAll() {
        return campaigns;
    }

    @Override
    public List<Campaign> findAllById(Iterable<String> strings) {
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
    public void delete(Campaign entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends Campaign> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Campaign> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Campaign> findAll(Pageable pageable) {
        return null;
    }
}
