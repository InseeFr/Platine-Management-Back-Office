package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.dto.SearchContactDto;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.*;
import java.util.function.Function;

public class ContactRepositoryStub implements ContactRepository {


    private Map<String, Contact> contacts = new HashMap<>();

    public void setContacts(List<Contact> contactList) {
        contacts.clear();
        for (Contact contact : contactList) {
            contacts.put(contact.getIdentifier(), contact);
        }
    }

    @Override
    public Page<Contact> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Contact findRandomContact() {
        return null;
    }

    @Override
    public String findRandomIdentifierContact() {
        return "";
    }

    @Override
    public Page<SearchContactDto> findByIdentifier(String param, Pageable pageable) {
        return null;
    }

    @Override
    public Page<SearchContactDto> findByEmail(String param, Pageable pageable) {
        return null;
    }

    @Override
    public Page<SearchContactDto> findByFirstNameLastName(String param, Pageable pageable) {
        return null;
    }

    @Override
    public void flush() {
        // Stub
    }

    @Override
    public <S extends Contact> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Contact> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Contact> entities) {
        // Stub
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {
        // Stub
    }

    @Override
    public void deleteAllInBatch() {
        // Stub
    }

    @Override
    public Contact getOne(String s) {
        return null;
    }

    @Override
    public Contact getById(String s) {
        return null;
    }

    @Override
    public Contact getReferenceById(String s) {
        return null;
    }

    @Override
    public <S extends Contact> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Contact> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Contact> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Contact> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Contact> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Contact> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Contact, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Contact> S save(S entity) {
        contacts.put(entity.getIdentifier(), entity);
        return entity;
    }

    @Override
    public <S extends Contact> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Contact> findById(String id) {
        return Optional.ofNullable(contacts.get(id));
    }

    @Override
    public boolean existsById(String id) {
        return contacts.containsKey(id);
    }

    @Override
    public List<Contact> findAll() {
        return List.of();
    }

    @Override
    public List<Contact> findAllById(Iterable<String> identifiers) {
        List<Contact> result = new ArrayList<>();
        for (String identifier : identifiers) {
            if (contacts.containsKey(identifier)) {
                result.add(contacts.get(identifier));
            }
        }
        return result;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {
        // Stub
    }

    @Override
    public void delete(Contact entity) {
        // Stub
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        // Stub
    }

    @Override
    public void deleteAll(Iterable<? extends Contact> entities) {
        // Stub
    }

    @Override
    public void deleteAll() {
        // Stub
    }

    @Override
    public List<Contact> findAll(Sort sort) {
        return List.of();
    }
}
