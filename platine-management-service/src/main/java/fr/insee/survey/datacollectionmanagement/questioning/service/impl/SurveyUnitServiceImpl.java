package fr.insee.survey.datacollectionmanagement.questioning.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactRepository;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.query.dto.SearchSurveyUnitContactDto;
import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ContactAccreditedToSurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ContactAccreditedToSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SurveyUnitDetailsDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitAddressRepository;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;
import fr.insee.survey.datacollectionmanagement.questioning.service.SurveyUnitService;
import fr.insee.survey.datacollectionmanagement.questioning.service.mapper.SurveyUnitMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurveyUnitServiceImpl implements SurveyUnitService {

    private final SurveyUnitRepository surveyUnitRepository;

    private final SurveyUnitAddressRepository surveyUnitAddressRepository;

    private final ContactRepository contactRepository;

    private final SurveyUnitMapper surveyUnitMapper;

    @Override
    public SurveyUnit findbyId(String idSu) {
        return surveyUnitRepository.findById(idSu)
                .orElseThrow(() -> new NotFoundException(String.format("SurveyUnit %s not found", idSu)));
    }

    @Override
    public Optional<SurveyUnit> findOptionalById(String idSu) {
        return surveyUnitRepository.findById(idSu);
    }

  /**
   * Trouve les identifiants d'unités d'enquête (UE) qui sont
   * demandés mais qui sont ABSENTS de la base de données.
   * Cette logique effectue une requête BDD unique (findExisting) puis calcule
   * la différence en mémoire (Set Subtraction).
   * @param identifiers L'ensemble des identifiants d'UE à vérifier.
   * @return Un ensemble contenant uniquement les identifiants manquants.
   */
  @Override
  public Set<String> findMissingIds(Set<String> identifiers) {
    if (identifiers == null || identifiers.isEmpty()) {
      return Set.of();
    }

    // 1. Appel en BDD optimisé (une seule requête IN) pour récupérer les identifiants qui EXISTENT.
    Set<String> existingIdentifiers = surveyUnitRepository.findExistingIds(identifiers);

    // 2. Calcul des MANQUANTS via soustraction d'ensembles.
    Set<String> missingIdentifiers = new HashSet<>(identifiers);
    existingIdentifiers.forEach(missingIdentifiers::remove);

    return missingIdentifiers;
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
        List<ContactAccreditedToSurveyUnit> contactAccreditedGroupByMainValue = surveyUnitRepository
                .findContactsAccreditedToSurveyUnit(id)
                .stream()
                .map(ContactAccreditedToSurveyUnitDto::toRecord)
                .toList();

        Set<String> contactIdentifiers = contactAccreditedGroupByMainValue.stream()
                .map(ContactAccreditedToSurveyUnit::contactId)
                .collect(Collectors.toSet());

        List<Contact> contacts = contactRepository.findAllById(new ArrayList<>(contactIdentifiers));

        Map<String, Contact> contactMap = contacts.stream()
                .collect(Collectors.toMap(Contact::getIdentifier, Function.identity()));

        return contactAccreditedGroupByMainValue.stream()
                .map(c -> {
                    Contact contact = contactMap.get(c.contactId());
                    if (contact == null) return null; // skip if contact not found

                    return SearchSurveyUnitContactDto.builder()
                            .identifier(contact.getIdentifier())
                            .function(contact.getFunction())
                            .firstName(contact.getFirstName())
                            .lastName(contact.getLastName())
                            .email(contact.getEmail())
                            .phoneNumber(contact.getPhone())
                            .city(contact.getAddress() != null ? contact.getAddress().getCityName() : null)
                            .campaigns(new HashSet<>(c.campaignIds()))
                            .isMain(c.isMain())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

    }

    @Override
    public SurveyUnitDetailsDto getDetailsById(String id) {
        SurveyUnit surveyUnit = findbyId(id);
        return surveyUnitMapper.toDto(surveyUnit);
    }

    @Override
    public List<String> getCampaignIds(String surveyUnitId) {
        return surveyUnitRepository.findCampaignIdsBySurveyUnitId(surveyUnitId);
    }
}
