package fr.insee.survey.datacollectionmanagement.contact.stub;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactEventRepository;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
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

public class ContactEventRepositoryStub implements ContactEventRepository {

    ArrayList<ContactEvent> contactEvents = new ArrayList<>();

    @Override
    public Set<ContactEvent> findByContact(Contact contact) {
        return Set.of();
    }

    @Override
    public List<ContactEvent> findByContactIdentifier(String contactId) {
        return List.of();
    }

    @Override
    public void flush() {
        // not used
    }

    @Override
    public <S extends ContactEvent> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends ContactEvent> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<ContactEvent> entities) {
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
    public ContactEvent getOne(Long aLong) {
        return null;
    }

    @Override
    public ContactEvent getById(Long aLong) {
        Optional<ContactEvent> contactEvent = contactEvents.stream().filter(ce -> ce.getId().equals(aLong)).findFirst();
        return contactEvent.orElseThrow(() -> new NotFoundException(String.format("ContactEvent not found for %s", aLong)));
    }

    @Override
    public ContactEvent getReferenceById(Long aLong) {
        return null;
    }

    @Override
    public <S extends ContactEvent> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends ContactEvent> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends ContactEvent> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends ContactEvent> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends ContactEvent> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends ContactEvent> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends ContactEvent, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends ContactEvent> S save(S entity) {
        contactEvents.add(entity);
        return  entity;
    }

    @Override
    public <S extends ContactEvent> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<ContactEvent> findById(Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Long aLong) {
        return false;
    }

    @Override
    public List<ContactEvent> findAll() {
        return List.of();
    }

    @Override
    public List<ContactEvent> findAllById(Iterable<Long> longs) {
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
    public void delete(ContactEvent entity) {
        // not used
    }

    @Override
    public void deleteAllById(Iterable<? extends Long> longs) {
        // not used
    }

    @Override
    public void deleteAll(Iterable<? extends ContactEvent> entities) {
        // not used
    }

    @Override
    public void deleteAll() {
        // not used
    }

    @Override
    public List<ContactEvent> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<ContactEvent> findAll(Pageable pageable) {
        return null;
    }
}
