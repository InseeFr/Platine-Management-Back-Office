package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Support;
import fr.insee.survey.datacollectionmanagement.metadata.repository.SupportRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.SupportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {

    private final SupportRepository supportRepository;

    public Support findById(String support) {

        return supportRepository.findById(support).orElseThrow(() -> new NotFoundException(String.format("Support %s not found", support)));
    }

    @Override
    public Page<Support> findAll(Pageable pageable) {
        return supportRepository.findAll(pageable);
    }

    @Override
    public Support insertOrUpdateSupport(Support support) {
        try {
            Support supportBase = findById(support.getId());
            log.info("Update support with the id {}", support.getId());
            support.setSources(supportBase.getSources());

        } catch (NotFoundException e) {
            log.info("Create support with the id {}", support.getId());
        }
        return supportRepository.save(support);

    }


}
