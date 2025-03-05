package fr.insee.survey.datacollectionmanagement.metadata.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Support;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupportService {

    Support findById(String support);

    Page<Support> findAll(Pageable pageable);

    Support insertOrUpdateSupport(Support support);

}
