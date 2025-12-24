package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.questioning.domain.InterrogationEventOrder;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.InterrogationEventOrderRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class InterrogationEventOrderRepositoryStub implements InterrogationEventOrderRepository {

    private static final int O_INITLA      = 1;
    private static final int O_PARTIEL_VAL = 2;
    private static final int O_EXPERT      = 2;
    private static final int O_REF_WAST    = 3;
    private static final int O_HC          = 4;

    private List<InterrogationEventOrder> interrogationEventOrders = List.of(
            order(TypeQuestioningEvent.valueOf("INITLA"),     O_INITLA),
            order(TypeQuestioningEvent.valueOf("PARTIELINT"), O_PARTIEL_VAL),
            order(TypeQuestioningEvent.valueOf("VALINT"),     O_PARTIEL_VAL),
            order(TypeQuestioningEvent.valueOf("RECUPAP"),     O_PARTIEL_VAL),
            order(TypeQuestioningEvent.valueOf("EXPERT"),     O_EXPERT),
            order(TypeQuestioningEvent.valueOf("ONGEXPERT"),  O_EXPERT),
            order(TypeQuestioningEvent.valueOf("VALID"),      O_EXPERT),
            order(TypeQuestioningEvent.valueOf("ENDEXPERT"),  O_EXPERT),
            order(TypeQuestioningEvent.valueOf("REFUSAL"),    O_REF_WAST),
            order(TypeQuestioningEvent.valueOf("WASTE"),      O_REF_WAST),
            order(TypeQuestioningEvent.valueOf("HC"),         O_HC)
    );

    private static InterrogationEventOrder order(TypeQuestioningEvent status, int valeur) {
        return new InterrogationEventOrder(null, status, valeur);
    }

    @Override
    public void flush() {
        //not used
    }

    @Override
    public <S extends InterrogationEventOrder> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends InterrogationEventOrder> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<InterrogationEventOrder> entities) {
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
    public InterrogationEventOrder getOne(Long aLong) {
        return null;
    }

    @Override
    public InterrogationEventOrder getById(Long aLong) {
        return null;
    }

    @Override
    public InterrogationEventOrder getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends InterrogationEventOrder> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends InterrogationEventOrder> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends InterrogationEventOrder> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends InterrogationEventOrder> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends InterrogationEventOrder> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends InterrogationEventOrder> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends InterrogationEventOrder, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends InterrogationEventOrder> S save(S entity) {
        return null;
    }

    @Override
    public <S extends InterrogationEventOrder> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<InterrogationEventOrder> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<InterrogationEventOrder> findAll() {
        return interrogationEventOrders;
    }

    @Override
    public List<InterrogationEventOrder> findAllById(Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Long aLong) {
        //not used
    }

    @Override
    public void delete(InterrogationEventOrder entity) {
        //not used
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        //not used
    }

    @Override
    public void deleteAll(Iterable<? extends InterrogationEventOrder> entities) {
        //not used
    }

    @Override
    public void deleteAll() {
        //not used
    }

    @Override
    public List<InterrogationEventOrder> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<InterrogationEventOrder> findAll(Pageable pageable) {
        return null;
    }
}
