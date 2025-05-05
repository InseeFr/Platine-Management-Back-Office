package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactRepository;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchSurveyUnitContactDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ContactAccreditedToSurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ContactAccreditedToSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitAddressRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyUnitServiceImpl implements SurveyUnitService {

    private final SurveyUnitRepository surveyUnitRepository;

    private final SurveyUnitAddressRepository surveyUnitAddressRepository;

    private final ContactRepository contactRepository;

    @Override
    public SurveyUnit findbyId(String idSu) {
        return surveyUnitRepository.findById(idSu).orElseThrow(() -> new NotFoundException(String.format("SurveyUnit" +
                                                                                                         " " +
                                                                                                         "%s not found", idSu)));
    }

    @Override
    public Optional<SurveyUnit> findOptionalById(String idSu) {
        return surveyUnitRepository.findById(idSu);
    }


    @Override
    public Page<SearchSurveyUnitDto> findbyIdentifier(String id, Pageable pageable) {
        return surveyUnitRepository.findByIdentifier(id, pageable);
    }

    @Override
    public Page<SearchSurveyUnitDto> findbyIdentificationCode(String identificationCode, Pageable pageable) {
        return surveyUnitRepository.findByIdentificationCode(identificationCode, pageable);
    }

    @Override
    public SurveyUnit saveSurveyUnitAndAddress(SurveyUnit surveyUnit) {

        if (surveyUnit.getSurveyUnitAddress() != null) {
            try {
                SurveyUnit existingSurveyUnit = findbyId(surveyUnit.getIdSu());
                if (existingSurveyUnit.getSurveyUnitAddress() != null) {
                    surveyUnit.getSurveyUnitAddress().setId(existingSurveyUnit.getSurveyUnitAddress().getId());
                }
            } catch (NotFoundException e) {
                log.debug("Survey unit does not exist");
            }
            surveyUnitAddressRepository.save(surveyUnit.getSurveyUnitAddress());

        }
        return surveyUnitRepository.save(surveyUnit);

    }

    @Override
    public Page<SearchSurveyUnitDto> findbyIdentificationName(String identificationName, Pageable pageable) {
        return surveyUnitRepository.findByIdentificationName(identificationName, pageable);
    }


    @Override
    public Page<SurveyUnit> findAll(Pageable pageable) {
        return surveyUnitRepository.findAll(pageable);
    }

    @Override
    public SurveyUnit saveSurveyUnit(SurveyUnit surveyUnit) {
        return surveyUnitRepository.save(surveyUnit);
    }


    @Override
    public void deleteSurveyUnit(String id) {
        surveyUnitRepository.deleteById(id);

    }

    @Override
    public Page<SearchSurveyUnitDto> findByParameter(String searchParam, Pageable pageable) {
        return surveyUnitRepository.findByParam(searchParam.toUpperCase(), pageable);
    }

    @Override
    public List<SearchSurveyUnitContactDto> findContactsBySurveyUnitId(String id) {
        List<ContactAccreditedToSurveyUnit> contactAccredited = surveyUnitRepository
                .findContactsAccreditedToSurveyUnit(id)
                .stream()
                .map(ContactAccreditedToSurveyUnitDto::toRecord)
                .toList();

        Map<String, List<ContactAccreditedToSurveyUnit>> contactAccreditedMap = contactAccredited
                .stream()
                .collect(Collectors.groupingBy(ContactAccreditedToSurveyUnit::contactId));

        List<String> contactIdentifiers = new ArrayList<>(contactAccreditedMap.keySet());
        List<Contact> contacts = contactRepository.findAllById(contactIdentifiers);

        return contacts.stream()
                .map(contact -> {
                    String identifier = contact.getIdentifier();
                    String city = (contact.getAddress() != null) ? contact.getAddress().getCityName() : null;
                    List<ContactAccreditedToSurveyUnit> records = contactAccreditedMap.getOrDefault(identifier, List.of());

                    Set<String> campaigns = records.stream()
                            .flatMap(c -> c.campaignIds().stream())
                            .collect(Collectors.toSet());

                    boolean isMain = records.stream()
                            .anyMatch(ContactAccreditedToSurveyUnit::isMain);

                    return SearchSurveyUnitContactDto.builder()
                            .identifier(identifier)
                            .function(contact.getFunction())
                            .firstName(contact.getFirstName())
                            .lastName(contact.getLastName())
                            .email(contact.getEmail())
                            .phoneNumber(contact.getPhone())
                            .city(city)
                            .campaigns(campaigns)
                            .isMain(isMain)
                            .build();
                }).toList();

    }

}
