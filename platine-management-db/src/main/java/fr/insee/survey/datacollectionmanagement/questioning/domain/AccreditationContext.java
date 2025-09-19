package fr.insee.survey.datacollectionmanagement.questioning.domain;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import java.util.Optional;

public record AccreditationContext(
    Questioning questioning,
    Contact contact,
    Optional<QuestioningAccreditation> existingMainAccreditation
) {}
