package fr.insee.survey.datacollectionmanagement.query.service.impl.stub;

import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.repository.ViewRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ViewRepositoryStub implements ViewRepository {

    ArrayList<View> views = new ArrayList<>();

    @Override
    public View findFirstByIdentifier(String identifier) {
        return null;
    }

    @Override
    public List<View> findByIdentifier(String identifier) {
        return List.of();
    }

    @Override
    public List<View> findDistinctViewByCampaignId(String campaignId) {
        return List.of();
    }

    @Override
    public List<String> findDistinctCampaignByIdentifier(String campaignId) {
        return List.of();
    }

    @Override
    public List<View> findByIdSu(String idSu) {
        return List.of();
    }

    @Override
    public List<View> findByIdSuContaining(String field) {
        return List.of();
    }

    @Override
    public Long countViewByIdentifierAndIdSuAndCampaignId(String identifier, String idSu, String campaignId) {
        return 0L;
    }

    @Override
    public List<View> findByIdentifierContainingAndIdSuNotNull(String identifier) {
        return List.of();
    }

    @Override
    public void deleteByIdentifier(String identifier) {
        // not used
    }

    @Override
    public List<View> findByIdentifierIn(List<String> identifiers) {
        return List.of();
    }

    @Override
    public Optional<View> findByIdentifierAndIdSuAndCampaignId(String identifier, String idSu, String campaignId) {
        return views.stream().filter(view ->
                view.getIdentifier().equals(identifier)
                && view.getIdSu().equals(idSu)
                && view.getCampaignId().equals(campaignId)).findFirst();
    }

    @Override
    public List<View> findByIdSuAndCampaignId(String idSu, String campaignId) {
        return List.of();
    }

    @Override
    public void flush() {
        // not used
    }

    @Override
    public <S extends View> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends View> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<View> entities) {
        // not used
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {
        // not used
    }

    @Override
    public void deleteAllInBatch() {
        // not used
    }

    @Override
    public View getOne(Long aLong) {
        return null;
    }

    @Override
    public View getById(Long aLong) {
        return null;
    }

    @Override
    public View getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends View> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends View> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends View> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends View> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends View> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends View> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends View, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends View> S save(S entity) {
        views.add(entity);
        return  entity;
    }

    @Override
    public <S extends View> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<View> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<View> findAll() {
        return List.of();
    }

    @Override
    public List<View> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {
        // not used
    }

    @Override
    public void delete(View entity) {
        // not used
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        // not used
    }

    @Override
    public void deleteAll(Iterable<? extends View> entities) {
        // not used
    }

    @Override
    public void deleteAll() {
        // not used
    }

    @Override
    public List<View> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<View> findAll(Pageable pageable) {
        return null;
    }
}
