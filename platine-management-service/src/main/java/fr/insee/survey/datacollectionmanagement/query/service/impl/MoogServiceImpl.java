package fr.insee.survey.datacollectionmanagement.query.service.impl;

import fr.insee.survey.datacollectionmanagement.constants.UserRoles;
import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.service.ContactService;
import fr.insee.survey.datacollectionmanagement.exception.NotFoundException;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Partitioning;
import fr.insee.survey.datacollectionmanagement.metadata.service.CampaignService;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogCampaignDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogExtractionRowDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogQuestioningEventDto;
import fr.insee.survey.datacollectionmanagement.query.dto.MoogSearchDto;
import fr.insee.survey.datacollectionmanagement.query.repository.MoogRepository;
import fr.insee.survey.datacollectionmanagement.query.service.MoogService;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.service.QuestioningService;
import fr.insee.survey.datacollectionmanagement.questioning.service.component.QuestioningUrlComponent;
import fr.insee.survey.datacollectionmanagement.util.JSONCollectionWrapper;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class MoogServiceImpl implements MoogService {

    private final ViewService viewService;

    private final ContactService contactService;

    private final CampaignService campaignService;

    private final MoogRepository moogRepository;

    private final QuestioningService questioningService;

    private final QuestioningUrlComponent questioningUrlComponent;

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
            Contact c = contactService.findByIdentifier(view.getIdentifier());
            Campaign camp = campaignService.findById(view.getCampaignId());
            MoogCampaignDto moogCampaign = new MoogCampaignDto();
            moogCampaign.setId(view.getCampaignId());
            moogCampaign.setLabel(camp.getCampaignWording());
            moogCampaign
                    .setCollectionEndDate(camp.getPartitionings().iterator().next().getClosingDate().getTime());
            moogCampaign
                    .setCollectionStartDate(camp.getPartitionings().iterator().next().getOpeningDate().getTime());
            moogSearchDto.setIdContact(view.getIdentifier());
            String address = createAddressMoog(c.getAddress());
            moogSearchDto.setAddress(address);
            moogSearchDto.setIdSu(view.getIdSu());
            moogSearchDto.setCampaign(moogCampaign);
            moogSearchDto.setFirstName(c.getFirstName());
            moogSearchDto.setLastname(c.getLastName());
            moogSearchDto.setSource(camp.getSurvey().getSource().getId());
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

        Campaign camp = campaignService.findById(campaign);
        MoogCampaignDto moogCampaign = new MoogCampaignDto();
        moogCampaign.setId(campaign);
        moogCampaign.setLabel(camp.getCampaignWording());
        moogCampaign.setCollectionEndDate(camp.getPartitionings().iterator().next().getClosingDate().getTime());
        moogCampaign.setCollectionStartDate(camp.getPartitionings().iterator().next().getOpeningDate().getTime());
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
        Campaign campaign = campaignService.findById(idCampaign);
        Set<Partitioning> setParts = campaign.getPartitionings();
        for (Partitioning part : setParts) {
            Optional<Questioning> optionalQuestioning = questioningService.findByIdPartitioningAndSurveyUnitIdSu(part.getId(), surveyUnitId);
            if (optionalQuestioning.isPresent()) {
                return questioningUrlComponent.getAccessUrl(UserRoles.REVIEWER, optionalQuestioning.get(), part);
            }
        }
        String msg = "0 questioning found for campaign " + idCampaign + " and survey unit " + surveyUnitId;
        log.error(msg);
        throw new NotFoundException(msg);
    }


}
