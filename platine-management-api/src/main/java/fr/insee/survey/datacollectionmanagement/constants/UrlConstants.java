package fr.insee.survey.datacollectionmanagement.constants;

public class UrlConstants {


    private UrlConstants() {
        throw new IllegalStateException("UrlConstants class");
    }

    // API CONTACTS DOMAIN
    public static final String API_CONTACTS = "/api/contacts/";
    public static final String API_CONTACTS_ALL = "/api/contacts";
    public static final String API_CONTACTS_ID = "/api/contacts/{id}";
    public static final String API_CONTACTS_ID_ADDRESS = "/api/contacts/{id}/address";
    public static final String API_CONTACTS_ID_CONTACTEVENTS = "/api/contacts/{id}/contact-events";
    public static final String API_CONTACT = "/api/contact/";
    public static final String API_CONTACT_CONTACTEVENTS = "/api/contact/contact-events";
    public static final String API_CONTACT_CONTACTEVENTS_PORTAL_PRIVILEGE = "/api/contact/contact-events/portal-privilege";
    public static final String API_MAIN_CONTACT_INTERROGATIONS_ASSIGN = "/api/interrogations/{interrogationId}/main-contact/{contactId}";
    public static final String API_NEW_MAIN_CONTACT_INTERROGATIONS_ASSIGN = "/api/contact/{interrogationId}/new-main-contact-with-interrogation";

    // API USER DOMAIN

    public static final String API_USERS_ALL = "/api/users";

    public static final String API_USERS_ALL_NO_PAGINATION ="/api/users/v2" ;
    public static final String API_USERS_ID = "/api/users/{id}";
    public static final String API_USERS_ID_USEREVENTS = "/api/users/{id}/user-events";
    public static final String API_USEREVENTS = "/api/users/contact-events";
    public static final String API_USEREVENTS_ID = "/api/users/user-events/{id}";
    public static final String API_SOURCE_ID_WALLET = "/api/source/{id}/wallet";


  //API QUESTIONING DOMAIN
    public static final String API_QUESTIONINGS = "/api/questionings";
    public static final String API_QUESTIONINGS_ID = "/api/questionings/{id}";
    public static final String API_QUESTIONINGSID = "/api/questionings/id";
    public static final String API_QUESTIONINGS_SEARCH = "/api/questionings/search";
    public static final String API_QUESTIONINGS_ID_ASSISTANCE = "/api/questioning/{id}/assistance";

    public static final String API_SURVEY_UNITS ="/api/survey-units";
    public static final String API_SURVEY_UNITS_ID = "/api/survey-units/{id}";
    public static final String API_SURVEY_UNITS_ID_CAMPAIGNS = "/api/survey-units/{id}/campaigns";
    public static final String API_SURVEY_UNITS_ID_QUESTIONINGS = "/api/survey-units/{id}/questionings";
    public static final String API_SURVEY_UNITS_ID_COMMENT = "/api/survey-units/{id}/comment";
    public static final String API_SURVEY_UNITS_ID_EVENTS = "/api/survey-units/{id}/events";

    public static final String API_QUESTIONINGS_ID_QUESTIONING_ACCREDITATIONS = "/api/questionings/{id}/questioning-accreditations";
    public static final String API_QUESTIONING_QUESTIONING_EVENTS = "/api/questionings/questioning-events";
    public static final String API_QUESTIONING_QUESTIONING_EVENTS_TYPE ="/api/questionings/questioning-events/{eventType}";
    public static final String API_QUESTIONING_QUESTIONING_COMMUNICATION ="/api/questionings/questioning-communication";
    public static final String API_QUESTIONING_ID_QUESTIONING_EVENTS = "/api/questionings/{id}/questioning-events";
    public static final String API_QUESTIONING_ID_EXPERT_EVENTS = "/api/questionings/{id}/expert-events";
    public static final String API_QUESTIONING_QUESTIONING_EVENTS_ID = "/api/questionings/questioning-events/{id}";
    public static final String API_QUESTIONING_ID_QUESTIONING_COMMUNICATIONS = "/api/questionings/{id}/questioning-communications";
    public static final String API_QUESTIONING_ID_COMMENT = "/api/questionings/{id}/comment";


    public static final String API_MAIN_CONTACT = "/api/main-contact";


    // API METADATA DOMAIN
    public static final String API_SOURCES = "/api/sources";
    public static final String API_SOURCES_ONGOING = "/api/sources/ongoing";
    public static final String API_SOURCES_ID = "/api/sources/{id}";
    public static final String API_SOURCES_ID_SURVEYS = "/api/sources/{id}/surveys";
    public static final String API_SURVEYS = "/api/surveys";
    public static final String API_SOURCE_ID_OPENED = "/api/sources/{id}/is-opened";
    public static final String API_SOURCES_ID_PARAMS = "/api/sources/{id}/params";


    public static final String API_SURVEYS_SEARCH = "/api/surveys/search";
    public static final String API_SURVEYS_ID = "/api/surveys/{id}";
    public static final String API_SURVEYS_ID_CAMPAIGNS = "/api/surveys/{id}/campaigns";



    public static final String API_SURVEYS_ID_CAMPAIGNS_PARTITIONINGS = "/api/surveys/{id}/campaigns-partitionings";
    public static final String API_CAMPAIGNS = "/api/campaigns";
    public static final String API_CAMPAIGNS_SEARCH = "/api/campaigns/search";
    public static final String API_CAMPAIGNS_ID_HEADER = "/api/campaigns/{id}/header";
    public static final String API_CAMPAIGNS_ID = "/api/campaigns/{id}";
    public static final String CAMPAIGNS_ID_ONGOING = "/campaigns/{id}/ongoing";
    public static final String API_CAMPAIGNS_ONGOING = "/api/campaigns/ongoing";
    public static final String API_CAMPAIGNS_COMMONS_ONGOING = "/api/campaigns/commons/ongoing";
    public static final String API_CAMPAIGNS_COMMONS_ID = "/api/campaigns/commons/{id}";
    public static final String API_CAMPAIGNS_ID_PARTITIONINGS = "/api/campaigns/{id}/partitionings";
    public static final String API_CAMPAIGNS_ID_PARAMS = "/api/campaigns/{id}/params";
    public static final String API_CAMPAIGN_ID_QUESTIONINGS_CSV = "/api/campaign/{campaignId}/questioning-csv";
    public static final String API_PARTITIONINGS_ID = "/api/partitionings/{id}";
    public static final String API_OWNERS = "/api/owners";
    public static final String API_OWNERS_ID = "/api/owners/{id}";
    public static final String API_OWNERS_ID_SOURCES = "/api/owners/{id}/sources";
    public static final String API_SUPPORTS = "/api/supports";
    public static final String API_SUPPORTS_ID = "/api/supports/{id}";
    public static final String API_PERIODICITIES = "/api/periodicities";
    public static final String API_PERIODICITIES_ID_PERIODS = "/api/periodicities/{periodicity}/periods";
    public static final String API_PERIODS = "/api/periods";

    public static final String API_METADATA_BUSINESS ="/api/metadata/business/{campaignId}";

    // API CROSS DOMAIN
    public static final String API_CHECK_HABILITATION = "/api/check-habilitation";
    public static final String API_CHECK_HABILITATION_V1 = "/api/v1/check-habilitation";
    public static final String MOOG_API_CAMPAIGNS = "/api/moog/campaigns";
    public static final String MOOG_API_CAMPAIGNS_ID = "/api/moog/campaigns/{id}";
    public static final String API_MOOG_SEARCH = "/api/moog/campaigns/survey-units";
    public static final String API_MOOG_EVENTS = "/api/moog/campaigns/{campaign}/survey-units/{id}/management-monitoring-infos";
    public static final String API_MOOG_MAIL = "/api/moog/contact/{id}/mail";
    public static final String MOOG_API_UPLOADS_ID = "/api/moog/uploads/{id}";
    public static final String MOOG_API_CAMPAIGN_UPLOADS = "/api/moog/campaigns/{idCampaign}/uploads";
    public static final String MOOG_API_CAMPAIGN_EXTRACTION = "/api/moog/campaigns/{idCampaign}/extraction";
    public static final String MOOG_API_CAMPAIGN_SURVEYUNITS_FOLLOWUP = "/api/moog/campaigns/{idCampaign}/survey-units/follow-up";

    public static final String MOOG_API_READONLY_URL = "/api/moog/readonly/campaigns/{idCampaign}/survey-unit/{surveyUnitId}";
    public static final String API_MOOG_DELETE_QUESTIONING_EVENT="/api/moog/management-monitoring-infos/{id}";
    public static final String API_CONTACTS_SEARCH = "/api/contacts/search";
    public static final String API_MY_QUESTIONINGS_ID = "/api/contacts/questionings";
    public static final String API_MY_QUESTIONNAIRES = "/api/contacts/questionnaires";
    public static final String API_SURVEY_UNITS_CONTACTS = "/api/survey-units/{id}/contacts";
    public static final String API_SURVEY_UNITS_PARTITIONINGS = "/api/survey-units/{id}/partitionings";
    public static final String API_USER_ROLE = "/api/user/role";


    public static final String API_SURVEY_UNITS_SEARCH = "/api/survey-units/search";




    // API WEBCLIENT
    public static final String API_WEBCLIENT_FOLLOWUP = "/api/partitionings/{idPartitioning}/survey-units/{idSu}/follow-up";
    public static final String API_WEBCLIENT_EXTRACT = "/api/partitionings/{idPartitioning}/survey-units/{idSu}/extract";
    public static final String API_WEBCLIENT_STATE = "/api/partitionings/{idPartitioning}/survey-units/{idSu}/state";
    public static final String API_WEBCLIENT_METADATA_ID = "/api/metadata/{id}";
    public static final String API_WEBCLIENT_QUESTIONINGS = "/api/questionings";
    public static final String API_WEBCLIENT_INFORMATIONS = "api/questioning/informations/{idCampaign}/{idUE}";
    public static final String API_WEBCLIENT_BUSINESS_MAIN_CONTACT ="/api/main-contact/{campaignId}/{surveyUnitId}";


    public static final String API_HEALTHCHECK = "/api/healthcheck";
    public static final String ACTUATOR = "/actuator/**";

}