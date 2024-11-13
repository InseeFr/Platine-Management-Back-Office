package fr.insee.survey.datacollectionmanagement.questioning.repository.dummy;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public class QuestioningRepositoryDummy implements QuestioningRepository {
    @Override
    public Set<Questioning> findByIdPartitioning(String idPartitioning) {
        return null;
    }

    @Override
    public Questioning findByIdPartitioningAndSurveyUnitIdSu(String idPartitioning, String surveyUnitIdSu) {
        return null;
    }

    @Override
    public Page<Questioning> findBySurveyUnitIdSuOrSurveyUnitIdentificationCodeOrQuestioningAccreditationsIdContact(String surveyUnitIdSu, String surveyUnitIdentificationCode, String idContact, Pageable pageable) {
        return null;
    }


    @Override
    public Set<Questioning> findBySurveyUnitIdSu(String idSu) {
        return null;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Questioning> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Questioning> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Questioning> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {

    }

    @Override
    public void deleteAllInBatch() {

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
        return null;
    }

    @Override
    public <S extends Questioning> List<S> findAll(Example<S> example, Sort sort) {
        return null;
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
        return null;
    }

    @Override
    public <S extends Questioning> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Questioning> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<Questioning> findAll() {
        return null;
    }

    @Override
    public List<Questioning> findAllById(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {

    }

    @Override
    public void delete(Questioning entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {

    }

    @Override
    public void deleteAll(Iterable<? extends Questioning> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Questioning> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Questioning> findAll(Pageable pageable) {
        return null;
    }

}
