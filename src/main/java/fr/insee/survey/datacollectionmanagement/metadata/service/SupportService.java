package fr.insee.survey.datacollectionmanagement.metadata.service;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Support;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupportService {

    Support findById(String support);

    Page<Support> findAll(Pageable pageable);

    Support insertOrUpdateSupport(Support support);

    void deleteSupportById(String id);

    void removeSourceFromSupport(Support support, Source source);

    void addSourceFromSupport(Support support, Source source);

}
