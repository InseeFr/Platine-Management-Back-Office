package fr.insee.survey.datacollectionmanagement.user.service.impl;

import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.user.domain.SourceAccreditation;
import fr.insee.survey.datacollectionmanagement.user.repository.SourceAccreditationRepository;
import fr.insee.survey.datacollectionmanagement.user.service.SourceAccreditationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SourceAccreditationServiceImpl implements SourceAccreditationService {

    private final SourceAccreditationRepository sourceAccreditationRepository;

    public List<SourceAccreditation> findByUserIdentifier(String id) {
        return sourceAccreditationRepository.findByIdUser(id);
    }

    @Override
    public Page<SourceAccreditation> findAll(Pageable pageable) {
        return sourceAccreditationRepository.findAll(pageable);
    }

    @Override
    public SourceAccreditation findById(Long id) {
        return sourceAccreditationRepository.findById(id).orElseThrow(()-> new NotFoundException(String.format("SourceAccreditation %s not found", id)));
    }

    @Override
    public SourceAccreditation saveSourceAccreditation(SourceAccreditation sourceAccreditation) {
        return sourceAccreditationRepository.save(sourceAccreditation);
    }

    @Override
    public void deleteAccreditation(SourceAccreditation acc) {
        sourceAccreditationRepository.deleteById(acc.getId());
    }
}
