package fr.insee.survey.datacollectionmanagement.query.service;

import fr.insee.survey.datacollectionmanagement.query.dto.SearchContactDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchContactService {

    /**
     * Search contact according to diffeent parameters
     * @param identifier contact identifier
     * @param name (first name or and lastName)
     * @param email contact email
     * @param city contact city
     * @param function contact function

     * @return
     */
    Page<SearchContactDto> searchContactCrossDomain(
        String identifier,
        String name,
        String email,
        String city,
        String function,
        Pageable pageable);
}
