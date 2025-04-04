package fr.insee.survey.datacollectionmanagement.metadata.service.impl;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Owner;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Source;
import fr.insee.survey.datacollectionmanagement.metadata.domain.Survey;
import fr.insee.survey.datacollectionmanagement.metadata.dto.OwnerBusinessDto;
import fr.insee.survey.datacollectionmanagement.metadata.dto.SurveyBusinessDto;
import fr.insee.survey.datacollectionmanagement.questioning.service.stub.CampaignServiceStub;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MetadataServiceImplTest {

    private final CampaignServiceStub campaignServiceStub= new CampaignServiceStub();
    private final MetadataServiceImpl metadataServiceImpl = new MetadataServiceImpl( campaignServiceStub);

    @Test
    void testGetSurveyBusinessDto_WithCompulsorySurvey() {
        Campaign campaign = new Campaign();
        Survey survey = new Survey();
        survey.setYear(2024);
        survey.setCompulsoryNature(true);
        survey.setShortObjectives("Short objectives");
        survey.setSurveyStatus("Active");
        survey.setDiffusionUrl("http://diffusion.url");
        survey.setNoticeUrl("http://notice.url");
        survey.setSpecimenUrl("http://specimen.url");
        campaign.setSurvey(survey);

        SurveyBusinessDto result = metadataServiceImpl.getSurveyBusinessDto(campaign);

        assertThat(result).isNotNull();
        assertThat(result.getYear()).isEqualTo(2024);
        assertThat(result.getCompulsaryNature()).isEqualTo("oui");
        assertThat(result.getShortObjectives()).isEqualTo("Short objectives");
        assertThat(result.getSurveyStatus()).isEqualTo("Active");
        assertThat(result.getDiffusionUrl()).isEqualTo("http://diffusion.url");
        assertThat(result.getNoticeUrl()).isEqualTo("http://notice.url");
        assertThat(result.getSpecimenUrl()).isEqualTo("http://specimen.url");
    }

    @Test
    void testGetOwnerBusinessDto_WithValidOwner() {
        Campaign campaign = new Campaign();
        Survey survey = new Survey();
        Source source = new Source();
        Owner owner = new Owner();

        owner.setDeterminer("Determiner");
        owner.setMinistry("Ministry");
        owner.setLabel("Label");

        source.setOwner(owner);
        survey.setSource(source);
        campaign.setSurvey(survey);

        OwnerBusinessDto result = metadataServiceImpl.getOwnerBusinessDto(campaign);

        assertThat(result).isNotNull();
        assertThat(result.getDeterminer()).isEqualTo("Determiner");
        assertThat(result.getMinistry()).isEqualTo("Ministry");
        assertThat(result.getLabel()).isEqualTo("Label");
    }
}
