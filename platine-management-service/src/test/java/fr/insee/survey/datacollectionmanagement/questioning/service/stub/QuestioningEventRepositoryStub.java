package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.domain.QuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningEventRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class QuestioningEventRepositoryStub implements QuestioningEventRepository {

    List<QuestioningEvent> questioningEvents = new ArrayList<>();


    @Override
    public List<QuestioningEvent> findByQuestioningIdAndType(UUID questioningId, TypeQuestioningEvent type) {
        return questioningEvents.stream()
                .filter(qe -> qe.getQuestioning().getId().equals(questioningId))
                .filter(qe -> qe.getType().equals(type))
                .toList();
    }

    @Override
    public Long countByUploadId(Long idupload) {
        return 0L;
    }

    @Override
    public List<QuestioningEvent> findByQuestioningId(UUID questioningId) {
        return List.of();
    }

    @Override
    public void flush() {
        //not used
    }

    @Override
    public <S extends QuestioningEvent> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends QuestioningEvent> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<QuestioningEvent> entities) {
        //not used
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Long> longs) {
        //not used
    }

    @Override
    public void deleteAllInBatch() {
        //not used
    }

    @Override
    public QuestioningEvent getOne(Long aLong) {
        return null;
    }

    @Override
    public QuestioningEvent getById(Long aLong) {
        return null;
    }

    @Override
    public QuestioningEvent getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends QuestioningEvent> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends QuestioningEvent> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends QuestioningEvent> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends QuestioningEvent> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends QuestioningEvent> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends QuestioningEvent> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends QuestioningEvent, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends QuestioningEvent> S save(S entity) {
        Questioning questioning = entity.getQuestioning();
        Set<QuestioningEvent> events = questioning.getQuestioningEvents();
        if (events == null) {
            events = new HashSet<>();
        }
        events.add(entity);
        questioning.setQuestioningEvents(events);
        entity.setQuestioning(questioning);
        questioningEvents.add(entity);
        return entity;
    }

    @Override
    public <S extends QuestioningEvent> List<S> saveAll(Iterable<S> entities) {
        Objects.requireNonNull(entities, "entities must not be null");

        List<S> saved = new ArrayList<>();
        for (S entity : entities) {
            if (entity == null) continue;
            saved.add(save(entity));
        }
        return saved;
    }

    @Override
    public Optional<QuestioningEvent> findById(Long aLong) {
        return questioningEvents.stream().filter(qe -> qe.getId().equals(aLong)).findFirst();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<QuestioningEvent> findAll() {
        return questioningEvents;
    }

    @Override
    public List<QuestioningEvent> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {
        questioningEvents.removeIf(qe -> qe.getId().equals(aLong));
    }

    @Override
    public void delete(QuestioningEvent entity) {
        //not used
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        //not used
    }

    @Override
    public void deleteAll(Iterable<? extends QuestioningEvent> entities) {
        //not used
    }

    @Override
    public void deleteAll() {
        //not used
    }

    @Override
    public List<QuestioningEvent> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<QuestioningEvent> findAll(Pageable pageable) {
        return null;
    }
}
