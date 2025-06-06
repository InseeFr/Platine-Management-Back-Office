package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class QuestioningRepositoryStub implements QuestioningRepository {

    ArrayList<Questioning> questionings = new ArrayList<>();

    @Override
    public Set<Questioning> findByIdPartitioning(String idPartitioning) {
        return Set.of();
    }

    @Override
    public Optional<Questioning> findByIdPartitioningAndSurveyUnitIdSu(String idPartitioning, String surveyUnitIdSu) {
        return Optional.empty();
    }

    @Override
    public List<Questioning> findQuestioningByParam(String searchParam) {
        return List.of();
    }

    @Override
    public List<Questioning> findQuestioningByCampaignIdAndSurveyUnitId(String campaignId, String surveyUnitId) {
        return List.of();
    }

    @Override
    public Set<Questioning> findBySurveyUnitIdSu(String idSu) {
        return Set.of();
    }

    @Override
    public boolean existsBySurveyUnitIdSu(String idSu) {
        if (idSu == null) {
            return false;
        }
        List<Questioning> byIdSu = questionings.stream().filter(q -> idSu.equals(q.getSurveyUnit().getIdSu())).toList();
        return !byIdSu.isEmpty();
    }

    @Override
    public Page<Questioning> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Long> findQuestioningIds(Pageable pageable) {
        return null;
    }

    @Override
    public List<Questioning> findQuestioningsByIds(List<Long> ids) {
        return List.of();
    }

    @Override
    public void flush() {
        // not used
    }

    @Override
    public <S extends Questioning> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Questioning> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Questioning> entities) {
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
    public Questioning getOne(Long aLong) {
        return null;
    }

    @Override
    public Questioning getById(Long aLong) {
        return null;
    }

    @Override
    public Questioning getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends Questioning> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Questioning> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Questioning> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Questioning> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Questioning> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Questioning> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Questioning, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Questioning> S save(S entity) {
        questionings.add(entity);
        return entity;
    }

    @Override
    public <S extends Questioning> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Questioning> findById(Long aLong) {
        return questionings.stream().filter(questioning -> questioning.getId().equals(aLong)).findFirst();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Questioning> findAll() {
        return List.of();
    }

    @Override
    public List<Questioning> findAllById(Iterable<Long> longs) {
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
    public void delete(Questioning entity) {
        // not used
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        // not used
    }

    @Override
    public void deleteAll(Iterable<? extends Questioning> entities) {
        // not used
    }

    @Override
    public void deleteAll() {
        // not used
    }

    @Override
    public List<Questioning> findAll(Sort sort) {
        return List.of();
    }
}
