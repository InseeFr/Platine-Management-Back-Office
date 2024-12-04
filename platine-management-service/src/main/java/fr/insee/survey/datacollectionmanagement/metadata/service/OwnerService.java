package fr.insee.survey.datacollectionmanagement.metadata.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Owner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OwnerService {

    Owner findById(String owner);

    Page<Owner> findAll(Pageable pageable);

    Owner insertOrUpdateOwner(Owner owner);


}
