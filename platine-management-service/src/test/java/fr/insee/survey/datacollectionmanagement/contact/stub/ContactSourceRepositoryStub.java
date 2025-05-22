package fr.insee.survey.datacollectionmanagement.contact.stub;

import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSource;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSourceId;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactSourceRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ContactSourceRepositoryStub implements ContactSourceRepository {

    ArrayList<ContactSource> contactSources = new ArrayList<>();

    @Override
    public void flush() {

    }

    @Override
    public <S extends ContactSource> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends ContactSource> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<ContactSource> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<ContactSourceId> contactSourceIds) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public ContactSource getOne(ContactSourceId contactSourceId) {
        return null;
    }

    @Override
    public ContactSource getById(ContactSourceId contactSourceId) {
        return null;
    }

    @Override
    public ContactSource getReferenceById(ContactSourceId contactSourceId) {
        return null;
    }

    @Override
    public <S extends ContactSource> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends ContactSource> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends ContactSource> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends ContactSource> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends ContactSource> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends ContactSource> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends ContactSource, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends ContactSource> S save(S entity) {
        return null;
    }

    @Override
    public <S extends ContactSource> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<ContactSource> findById(ContactSourceId contactSourceId) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(ContactSourceId contactSourceId) {
        return false;
    }

    @Override
    public List<ContactSource> findAll() {
        return List.of();
    }

    @Override
    public List<ContactSource> findAllById(Iterable<ContactSourceId> contactSourceIds) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(ContactSourceId contactSourceId) {

    }

    @Override
    public void delete(ContactSource entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends ContactSourceId> contactSourceIds) {

    }

    @Override
    public void deleteAll(Iterable<? extends ContactSource> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<ContactSource> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<ContactSource> findAll(Pageable pageable) {
        return null;
    }
}
