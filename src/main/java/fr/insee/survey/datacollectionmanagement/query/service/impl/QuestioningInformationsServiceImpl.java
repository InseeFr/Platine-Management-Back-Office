package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.query.domain.QuestioningInformations;
import fr.insee.survey.datacollectionmanagement.query.dto.AddressInformationsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.ContactInformationsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.QuestioningInformationsDto;
import fr.insee.survey.datacollectionmanagement.query.dto.SurveyUnitInformationsDto;
import fr.insee.survey.datacollectionmanagement.query.repository.InformationsRepository;
import fr.insee.survey.datacollectionmanagement.query.service.QuestioningInformationsService;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;


@Service
@RequiredArgsConstructor
public class QuestioningInformationsServiceImpl implements QuestioningInformationsService {

    private final InformationsRepository informationsRepository;

    private final CampaignService campaignService;

    private final QuestioningService questioningService;


    @Override
    public QuestioningInformationsDto findQuestioningInformationsDtoReviewer(String idCampaign, String idsu) {


        List<Partitioning> listParts = campaignService.findById(idCampaign).getPartitionings().
                stream().filter(p -> questioningService.findByIdPartitioningAndSurveyUnitIdSu(p.getId(), idsu) != null).toList();

        if (listParts.isEmpty()) {
            throw new NotFoundException(String.format("Questioning not found for campaign %s and survey unit %s", idCampaign, idsu));
        }
        String partId = listParts.get(0).getId();
        QuestioningInformations infos = informationsRepository.findQuestioningInformationsReviewer(partId, idsu);
        return mapQuestioningInformationsDto(infos);
    }

    @Override
    public QuestioningInformationsDto findQuestioningInformationsDtoInterviewer(String idCampaign, String idsu, String contactId) {


        List<Partitioning> listParts = campaignService.findById(idCampaign).getPartitionings().
                stream().filter(p -> questioningService.findByIdPartitioningAndSurveyUnitIdSu(p.getId(), idsu) != null).toList();

        if (listParts.isEmpty()) {
            throw new NotFoundException(String.format("Questioning not found for campaign %s and survey unit %s", idCampaign, idsu));
        }
        String partId = listParts.get(0).getId();
        QuestioningInformations infos = informationsRepository.findQuestioningInformationsInterviewer(partId, idsu, contactId);
        return mapQuestioningInformationsDto(infos);
    }




    private QuestioningInformationsDto mapQuestioningInformationsDto(QuestioningInformations infos) {
        QuestioningInformationsDto questioningInformationsDto = new QuestioningInformationsDto();

        // Map basic fields
        questioningInformationsDto.setReturnDate(infos.getReturnDate());
        questioningInformationsDto.setLogo(infos.getLogo());
        questioningInformationsDto.setUrlLogout("/" + infos.getSourceId());
        questioningInformationsDto.setUrlAssistance(URLEncoder.encode("/" + infos.getSourceId() + "/contacter-assistance/auth?questioningId=" + infos.getQuestioningId(), StandardCharsets.UTF_8));

        // Map ContactInformationsDto
        ContactInformationsDto contactDto = new ContactInformationsDto();
        contactDto.setIdentity(getFormattedCivility(infos.getGender(), infos.getFirstName(), infos.getLastName()));
        contactDto.setEmail(infos.getEmail());
        contactDto.setPhoneNumber(getFormattedPhone(infos.getPhone(), infos.getPhone2()));
        contactDto.setUsualCompanyName(infos.getUsualCompanyName());

        AddressInformationsDto adressDto = getAddressInformationsDto(infos);

        contactDto.setAddressInformationsDto(adressDto);
        questioningInformationsDto.setContactInformationsDto(contactDto);

        // Map SurveyUnitInformationsDto
        SurveyUnitInformationsDto surveyUnitDto = new SurveyUnitInformationsDto();
        surveyUnitDto.setLabel(infos.getLabel());
        surveyUnitDto.setSurveyUnitId(infos.getIdentificationCode());
        surveyUnitDto.setIdentificationName(infos.getIdentificationName());
        questioningInformationsDto.setSurveyUnitInformationsDto(surveyUnitDto);
        return questioningInformationsDto;
    }

    protected String getFormattedPhone(String phone, String phone2) {
        if (phone != null && StringUtils.isNotBlank(phone)) {
            return phone;
        }
        if (phone2 != null && StringUtils.isNotBlank(phone2)) {
            return phone2;
        }
        return null;
    }

    private static AddressInformationsDto getAddressInformationsDto(QuestioningInformations infos) {
        AddressInformationsDto addressDto = new AddressInformationsDto();
        addressDto.setCountryName(infos.getCountryName());
        addressDto.setStreetName(infos.getStreetName());
        addressDto.setStreetType(infos.getStreetType());
        addressDto.setStreetNumber(infos.getStreetNumber());
        addressDto.setRepetitionIndex(infos.getRepetitionIndex());
        addressDto.setZipCode(infos.getZipCode());
        addressDto.setCityName(infos.getCityName());
        addressDto.setCedexCode(infos.getCedexCode());
        addressDto.setCedexName(infos.getCedexName());
        return addressDto;
    }

    protected static String getFormattedCivility(String gender, String firstName, String lastName) {
        String formattedGender = "";
        String formattedFirstName = "";
        String formattedLastName = "";

        if (gender != null) {
            if (gender.equalsIgnoreCase(Contact.Gender.Male.name())) {
                formattedGender = "M.";
            }
            if (gender.equalsIgnoreCase((Contact.Gender.Female.name()))) {
                formattedGender = "Mme";
            }
        }

        if (lastName != null && !lastName.trim().isEmpty()) {
            formattedLastName = lastName.trim();
        }
        if (firstName != null && !firstName.trim().isEmpty()) {
            formattedFirstName = firstName.trim();
        }

        StringBuilder formattedCivility = new StringBuilder();
        if (!formattedGender.isEmpty()) {
            formattedCivility.append(formattedGender).append(" ");
        }
        if (!formattedFirstName.isEmpty()) {
            formattedCivility.append(formattedFirstName).append(" ");
        }
        if (!formattedLastName.isEmpty()) {
            formattedCivility.append(formattedLastName);
        }

        return formattedCivility.toString().trim();
    }
}
