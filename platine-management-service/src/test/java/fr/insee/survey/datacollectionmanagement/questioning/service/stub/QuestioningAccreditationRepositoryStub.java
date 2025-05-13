package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.query.dto.MyQuestionnaireDetailsDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningAccreditation;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningAccreditationRepository;
import lombok.Setter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Setter
public class QuestioningAccreditationRepositoryStub implements QuestioningAccreditationRepository {

    private List<MyQuestionnaireDetailsDto> myQuestionnaireDetailsDto;

    @Override
    public List<QuestioningAccreditation> findByIdContact(String idContact) {
        return List.of();
    }

    @Override
    public List<MyQuestionnaireDetailsDto> findQuestionnaireDetailsByIdec(String idec) {
        return myQuestionnaireDetailsDto;
    }

    @Override
    public List<QuestioningAccreditation> findAccreditationByQuestioningId(Long idQuestioning) {
        return List.of();
    }


    @Override
    public void flush() {
        // not used
    }

    @Override
    public <S extends QuestioningAccreditation> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends QuestioningAccreditation> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<QuestioningAccreditation> entities) {
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
    public QuestioningAccreditation getOne(Long aLong) {
        return null;
    }

    @Override
    public QuestioningAccreditation getById(Long aLong) {
        return null;
    }

    @Override
    public QuestioningAccreditation getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends QuestioningAccreditation> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends QuestioningAccreditation> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends QuestioningAccreditation> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends QuestioningAccreditation> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends QuestioningAccreditation> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends QuestioningAccreditation> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends QuestioningAccreditation, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends QuestioningAccreditation> S save(S entity) {
        return null;
    }

    @Override
    public <S extends QuestioningAccreditation> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<QuestioningAccreditation> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<QuestioningAccreditation> findAll() {
        return List.of();
    }

    @Override
    public List<QuestioningAccreditation> findAllById(Iterable<Long> longs) {
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
    public void delete(QuestioningAccreditation entity) {
        // not used
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        // not used
    }

    @Override
    public void deleteAll(Iterable<? extends QuestioningAccreditation> entities) {
        // not used
    }

    @Override
    public void deleteAll() {
        // not used
    }

    @Override
    public List<QuestioningAccreditation> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<QuestioningAccreditation> findAll(Pageable pageable) {
        return null;
    }
}
