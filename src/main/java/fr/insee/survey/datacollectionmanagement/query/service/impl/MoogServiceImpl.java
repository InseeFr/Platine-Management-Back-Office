package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.config.ApplicationConfig;
import fr.insee.survey.datacollectionmanagement.config.JSONCollectionWrapper;
import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.metadata.service.PartitioningService;
import fr.insee.survey.datacollectionmanagement.query.domain.MoogCampaign;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogExtractionRowDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogQuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogSearchDto;
import fr.insee.survey.datacollectionmanagement.query.repository.MoogRepository;
import fr.insee.survey.datacollectionmanagement.query.service.MoogService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.*;

@Service
@Slf4j
public class MoogServiceImpl implements MoogService {

    public static final String READONLY_QUESTIONNAIRE = "/readonly/questionnaire/";
    public static final String UNITE_ENQUETEE = "/unite-enquetee/";
    @Autowired
    private ViewService viewService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private MoogRepository moogRepository;

    @Autowired
    QuestioningService questioningService;

    @Autowired
    PartitioningService partitioningService;

    @Autowired
    ApplicationConfig applicationConfig;

    @Override
    public List<View> moogSearch(String field) {

        List<View> listView = new ArrayList<>();
        listView.addAll(viewService.findByIdentifierContainingAndIdSuNotNull(field));
        listView.addAll(viewService.findViewByIdSuContaining(field));
        return listView;
    }

    public List<MoogSearchDto> transformListViewToListMoogSearchDto(List<View> listView) {
        List<MoogSearchDto> listResult = new ArrayList<>();
        for (View view : listView) {
            MoogSearchDto moogSearchDto = new MoogSearchDto();
            Optional<Contact> c = contactService.findByIdentifier(view.getIdentifier());
            Optional<Campaign> camp = campaignService.findById(view.getCampaignId());
            if (!camp.isPresent()) {
                throw new NoSuchElementException("campaign does not exist");
            }
            if (!c.isPresent()) {
                throw new NoSuchElementException("contact does not exist");
            }
            MoogCampaign moogCampaign = new MoogCampaign();
            moogCampaign.setId(view.getCampaignId());
            moogCampaign.setLabel(camp.get().getCampaignWording());
            moogCampaign
                    .setCollectionEndDate(camp.get().getPartitionings().iterator().next().getClosingDate().getTime());
            moogCampaign
                    .setCollectionStartDate(camp.get().getPartitionings().iterator().next().getOpeningDate().getTime());
            moogSearchDto.setIdContact(view.getIdentifier());
            String address = createAddressMoog(c.get().getAddress());
            moogSearchDto.setAddress(address);
            moogSearchDto.setIdSu(view.getIdSu());
            moogSearchDto.setCampaign(moogCampaign);
            moogSearchDto.setFirstName(c.get().getFirstName());
            moogSearchDto.setLastname(c.get().getLastName());
            moogSearchDto.setSource(camp.get().getSurvey().getSource().getId());
            listResult.add(moogSearchDto);
        }
        return listResult;
    }

    protected String createAddressMoog(Address address) {
        String zipCode = address.getZipCode();
        String city = address.getCityName();
        return StringUtils.trim(String.join(" ", valueNotNull(zipCode), valueNotNull(city)));
    }

    private String valueNotNull(String value) {
        return value == null || value.isBlank() ? "" : value;
    }

    @Override
    public List<MoogQuestioningEventDto> getMoogEvents(String campaign, String idSu) {

        List<MoogQuestioningEventDto> moogEvents = moogRepository.getEventsByIdSuByCampaign(campaign, idSu);

        Optional<Campaign> camp = campaignService.findById(campaign);
        if (!camp.isPresent()) {
            throw new NoSuchElementException("campaign does not exist");
        }
        MoogCampaign moogCampaign = new MoogCampaign();
        moogCampaign.setId(campaign);
        moogCampaign.setLabel(camp.get().getCampaignWording());
        moogCampaign.setCollectionEndDate(camp.get().getPartitionings().iterator().next().getClosingDate().getTime());
        moogCampaign.setCollectionStartDate(camp.get().getPartitionings().iterator().next().getOpeningDate().getTime());
        MoogSearchDto surveyUnit = new MoogSearchDto();
        surveyUnit.setCampaign(moogCampaign);
        moogEvents.stream().forEach(e -> e.setSurveyUnit(surveyUnit));

        return moogEvents;
    }

    public JSONCollectionWrapper<MoogExtractionRowDto> getExtraction(String idCampaign) {

        return new JSONCollectionWrapper<>(moogRepository.getExtraction(idCampaign));
    }

    public Collection<MoogExtractionRowDto> getSurveyUnitsToFollowUp(String idCampaign) {
        return moogRepository.getSurveyUnitToFollowUp(idCampaign);
    }

    @Override
    public String getReadOnlyUrl(String idCampaign, String surveyUnitId) throws NotFoundException {
        Optional<Campaign> campaign = campaignService.findById(idCampaign);
        if (!campaign.isPresent()) {
            throw new NotFoundException("Campaign not found");
        }
        Set<Partitioning> setParts = campaign.get().getPartitionings();
        Questioning questioning = null;
        for (Partitioning part : setParts) {
            Questioning qTemp = questioningService.findByIdPartitioningAndSurveyUnitIdSu(part.getId(), surveyUnitId);
            if (qTemp != null) {
                questioning = qTemp;
                break;
            }
        }
        if (questioning != null) {
            return applicationConfig.getQuestioningUrl() + READONLY_QUESTIONNAIRE + questioning.getModelName()
                    + UNITE_ENQUETEE + surveyUnitId;
        }
        String msg = "0 questioning found for campaign " + idCampaign + " and survey unit " + surveyUnitId;
        log.error(msg);
        throw new NotFoundException(msg);
    }


}
