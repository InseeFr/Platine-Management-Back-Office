package fr.insee.survey.datacollectionmanagement.contact.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactSource;
import fr.insee.survey.datacollectionmanagement.contact.dto.BusinessAddressDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.BusinessContactDto;
import fr.insee.survey.datacollectionmanagement.contact.dto.BusinessContactsDto;
import fr.insee.survey.datacollectionmanagement.contact.enums.GenderEnum;
import fr.insee.survey.datacollectionmanagement.contact.service.BusinessContactService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactSourceService;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusinessContactServiceImpl implements BusinessContactService {

    private final QuestioningService questioningService;

    private final CampaignService campaignService;

    private final ContactService contactService;

    private final ContactSourceService contactSourceService;


    @Override
    public BusinessContactsDto findMainContactByCampaignAndSurveyUnit(String campaignId, String surveyUnitId) {
        Campaign campaign = campaignService.findById(campaignId);
        String sourceId = Optional.of(campaign)
                .map(Campaign::getSurvey)
                .map(Survey::getSource)
                .map(Source::getId)
                .orElseThrow(() -> new IllegalStateException("..."));

        ContactSource contactSource = contactSourceService
                .findMainContactSourceBySourceAndSurveyUnit(sourceId, surveyUnitId);

        List<BusinessContactDto> businessContactDtoList = (contactSource != null && contactSource.getId() != null)
                ? List.of(getBusinessContactFromIdentifier(contactSource.getId().getContactId()))
                : Collections.emptyList();
        int size = businessContactDtoList.size();

        return BusinessContactsDto.builder()
                .count(size)
                .start(size)
                .hit(size)
                .businessContactDtoList(businessContactDtoList)
                .build();
    }

    private BusinessContactDto getBusinessContactFromIdentifier(@NonNull String contactId) {
        Contact contact = contactService.findByIdentifier(contactId);
        BusinessContactDto businessContactDto = new BusinessContactDto();
        businessContactDto.setIdeC(contact.getIdentifier());
        businessContactDto.setAdresseMessagerie(contact.getEmail());
        businessContactDto.setNom(contact.getLastName());
        businessContactDto.setPrenom(contact.getFirstName());
        businessContactDto.setFonction(contact.getFunction());
        businessContactDto.setRaisonSocialeUsuelle(contact.getUsualCompanyName());
        businessContactDto.setNumeroTelephone(contact.getPhone());
        businessContactDto.setTelephonePortable(contact.getOtherPhone());
        businessContactDto.setFacSimile(null);
        businessContactDto.setCommentaire(contact.getComment());
        businessContactDto.setEcivilite(getBusinessCivility(contact.getGender()));

        BusinessAddressDto businessAddressDto = getBusinessAddressDto(contact);
        businessContactDto.setBusinessAddressDto(businessAddressDto);
        return businessContactDto;
    }

    private static BusinessAddressDto getBusinessAddressDto(Contact contact) {
        BusinessAddressDto businessAddressDto = new BusinessAddressDto();
        if (contact.getAddress() != null) {
            businessAddressDto.setLibellePays(contact.getAddress().getCountryName());
            businessAddressDto.setNumeroVoie(contact.getAddress().getStreetNumber());
            businessAddressDto.setIndiceRepetition(contact.getAddress().getRepetitionIndex());
            businessAddressDto.setTypeVoie(contact.getAddress().getStreetType());
            businessAddressDto.setLibelleVoie(contact.getAddress().getStreetName());
            businessAddressDto.setComplementAdresse(contact.getAddress().getAddressSupplement());
            businessAddressDto.setMentionSpeciale(contact.getAddress().getSpecialDistribution());
            businessAddressDto.setCodePostal(contact.getAddress().getZipCode());
            businessAddressDto.setLibelleCommune(contact.getAddress().getCityName());
            businessAddressDto.setBureauDistributeur(contact.getAddress().getSpecialDistribution());
            businessAddressDto.setCodeCedex(contact.getAddress().getCedexCode());
            businessAddressDto.setLibelleCedex(contact.getAddress().getCedexName());
            businessAddressDto.setCodeCommune(null);
        }
        return businessAddressDto;
    }

    private String getBusinessCivility(GenderEnum gender) {
        if (gender.equals(GenderEnum.Female))
            return "MADAME";
        if (gender.equals(GenderEnum.Male))
            return "MONSIEUR";
        return "";
    }
}
