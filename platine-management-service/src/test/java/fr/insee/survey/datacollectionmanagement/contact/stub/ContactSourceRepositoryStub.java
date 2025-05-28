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
        // not used
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
        // not used
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<ContactSourceId> contactSourceIds) {
        // not used
    }

    @Override
    public void deleteAllInBatch() {
        // not used
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
        return contactSources.size();
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
        Optional<ContactSource> contactSource = contactSources.stream().filter(e -> e.getId().equals(entity.getId())).findFirst();
        contactSource.ifPresent(source -> contactSources.remove(source));
        contactSources.add(entity);
        return entity;
    }

    @Override
    public <S extends ContactSource> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<ContactSource> findById(ContactSourceId contactSourceId) {
        return contactSources.stream().filter(contactSource -> contactSource.getId().equals(contactSourceId)).findFirst();
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
        return contactSources.size();
    }

    @Override
    public void deleteById(ContactSourceId contactSourceId) {
        Optional<ContactSource> contactSource = findById(contactSourceId);
        contactSource.ifPresent(this::delete);
    }

    @Override
    public void delete(ContactSource entity) {
        contactSources.remove(entity);
    }

    @Override
    public void deleteAllById(Iterable<? extends ContactSourceId> contactSourceIds) {
        // not used
    }

    @Override
    public void deleteAll(Iterable<? extends ContactSource> entities) {
        // not used
    }

    @Override
    public void deleteAll() {
        // not used
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
