package fr.insee.survey.datacollectionmanagement.contact.repository;

import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSource;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSourceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactSourceRepository extends JpaRepository<ContactSource, ContactSourceId> {
}
