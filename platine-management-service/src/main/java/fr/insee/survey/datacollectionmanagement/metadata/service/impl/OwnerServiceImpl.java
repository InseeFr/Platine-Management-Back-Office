package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Owner;
import fr.insee.survey.datacollectionmanagement.metadata.repository.OwnerRepository;
import fr.insee.survey.datacollectionmanagement.metadata.service.OwnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OwnerServiceImpl implements OwnerService {

    private final OwnerRepository ownerRepository;

    public Owner findById(String owner) {
        return ownerRepository.findById(owner).orElseThrow(() -> new NotFoundException(String.format("Owner %s not found", owner)));
    }

    @Override
    public Page<Owner> findAll(Pageable pageable) {
        return ownerRepository.findAll(pageable);
    }

    @Override
    public Owner insertOrUpdateOwner(Owner owner) {

        try {
            Owner ownerBase = findById(owner.getId());
            log.info("Update owner with the id {}", ownerBase.getId());
            return ownerRepository.save(owner);
        } catch (NotFoundException e) {
            log.info("Create owner with the id {}", owner.getId());
            return ownerRepository.save(owner);
        }


    }

}
