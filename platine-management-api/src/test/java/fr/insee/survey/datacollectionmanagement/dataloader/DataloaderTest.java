package fr.insee.survey.datacollectionmanagement.dataloader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.enums.ContactEventTypeEnum;
import fr.insee.survey.datacollectionmanagement.contact.enums.GenderEnum;
import fr.insee.survey.datacollectionmanagement.contact.repository.AddressRepository;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactEventRepository;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactRepository;
import fr.insee.survey.datacollectionmanagement.metadata.domain.*;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodEnum;
import fr.insee.survey.datacollectionmanagement.metadata.enums.PeriodicityEnum;
import fr.insee.survey.datacollectionmanagement.metadata.repository.*;
import fr.insee.survey.datacollectionmanagement.questioning.domain.*;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.questioning.repository.*;
import fr.insee.survey.datacollectionmanagement.user.domain.User;
import fr.insee.survey.datacollectionmanagement.user.enums.UserRoleTypeEnum;
import fr.insee.survey.datacollectionmanagement.user.service.UserService;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.repository.ViewRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
@Profile("test")
@Slf4j
public class DataloaderTest {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ContactEventRepository contactEventRepository;

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private SupportRepository supportRepository;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyUnitRepository surveyUnitRepository;

    @Autowired
    private QuestioningRepository questioningRepository;

    @Autowired
    private QuestioningAccreditationRepository questioningAccreditationRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private PartitioningRepository partitioningRepository;

    @Autowired
    private EventOrderRepository orderRepository;

    @Autowired
    private QuestioningEventRepository questioningEventRepository;

    @Autowired
    private ViewRepository viewRepository;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void init() throws ParseException {

        Faker faker = new Faker();

        initOrder();
        initContact();
        initMetadata();
        initQuestioning(faker);
        initView();
        initUser();

    }

    private void initUser() {
        User user = new User();
        user.setIdentifier("USER1");
        user.setRole(UserRoleTypeEnum.ASSISTANCE);
        userService.createUser(user, null);
    }

    private void initOrder() {

        Long nbExistingOrders = orderRepository.count();

        if (nbExistingOrders == 0) {
            // Creating table order
            log.info("loading eventorder data");
            orderRepository.saveAndFlush(
                    new EventOrder(Long.parseLong("8"), TypeQuestioningEvent.REFUSAL.toString().toString(), 8));
            orderRepository
                    .saveAndFlush(new EventOrder(Long.parseLong("7"), TypeQuestioningEvent.VALINT.toString(), 7));
            orderRepository
                    .saveAndFlush(new EventOrder(Long.parseLong("6"), TypeQuestioningEvent.VALPAP.toString(), 6));
            orderRepository.saveAndFlush(new EventOrder(Long.parseLong("5"), TypeQuestioningEvent.HC.toString(), 5));
            orderRepository
                    .saveAndFlush(new EventOrder(Long.parseLong("4"), TypeQuestioningEvent.PARTIELINT.toString(), 4));
            orderRepository.saveAndFlush(new EventOrder(Long.parseLong("3"), TypeQuestioningEvent.WASTE.toString(), 3));
            orderRepository.saveAndFlush(new EventOrder(Long.parseLong("2"), TypeQuestioningEvent.PND.toString(), 2));
            orderRepository
                    .saveAndFlush(new EventOrder(Long.parseLong("1"), TypeQuestioningEvent.INITLA.toString(), 1));
        }
    }

    private void initContact() {

        createContactAddressAndEvents(1);
        createContactAddressAndEvents(2);
        createContactAddressAndEvents(3);
        createContactAddressAndEvents(4);
        createContactAddressAndEvents(5);

        log.info(contactRepository.count() + " contacts exist in database");

    }

    private void createContactAddressAndEvents(int i) {

        // Address
        Address address = createAddress(i);
        Contact contact = createContact(i);
        contact.setAddress(address);
        createContactEvent(contact);
        log.info("Contact created : {}", contact.toString());
        contactRepository.save(contact);
    }

    private void createContactEvent(Contact contact) {
        ContactEvent contactEvent = new ContactEvent();
        contactRepository.save(contact);
        contactEvent.setType(ContactEventTypeEnum.create);
        contactEvent.setEventDate(new Date());
        contactEvent.setContact(contact);
        String json = "{\"contact_identifier\":\"" + contact.getIdentifier() + "\",\"name\":\"" + contact.getLastName()
                + "\"}";
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readTree(json);
            contactEvent.setPayload(node);
        } catch (JsonProcessingException e) {
            log.error("json error");
        }
        contactEventRepository.save(contactEvent);
        Set<ContactEvent> setContactEvents = new HashSet<>();
        setContactEvents.add(contactEvent);
        contact.setContactEvents(setContactEvents);
    }

    private Address createAddress(int i) {
        Address address = new Address();
        address.setCountryName("country" + 1);
        address.setStreetNumber(Integer.toString(i));
        address.setStreetName("street name" + i);
        address.setZipCode(Integer.toString(1000 * i));
        address.setCityName("city" + i);
        addressRepository.save(address);
        return address;

    }

    private Contact createContact(int i) {
        Contact contact = new Contact();
        contact.setIdentifier("CONT" + Integer.toString(i));
        contact.setFirstName("firstName" + i);
        contact.setLastName("lastName" + i);
        contact.setEmail(contact.getFirstName() + contact.getLastName() + "@test.com");
        if (i % 2 == 0)
            contact.setGender(GenderEnum.Female);
        if (i % 2 != 0)
            contact.setGender(GenderEnum.Male);
        return contact;
    }

    private void initMetadata() throws ParseException {

        int year = 2023;

        Owner ownerInsee = new Owner();
        ownerInsee.setId("Insee");
        ownerInsee.setLabel("Insee");
        Set<Source> setSourcesInsee = new HashSet<>();

        while (sourceRepository.count() < 2) {

            Source source = new Source();
            String sourceName = "SOURCE" + Math.addExact(sourceRepository.count(), 1);
            if (!StringUtils.contains(sourceName, " ") && sourceRepository.findById(sourceName).isEmpty()) {

                source.setId(sourceName);
                source.setLongWording("Long wording of " + sourceName + " ?");
                source.setShortWording("Short wording of " + sourceName);
                source.setPeriodicity(PeriodicityEnum.T);
                sourceRepository.save(source);
                Set<Survey> setSurveys = new HashSet<>();
                setSourcesInsee.add(source);

                for (int j = 0; j < 2; j++) {

                    Survey survey = new Survey();
                    String id = sourceName + (year - j);
                    survey.setId(id);
                    survey.setYear(year - j);
                    survey.setLongObjectives("The purpose of this survey is to find out everything you can about "
                            + sourceName
                            + ". Your response is essential to ensure the quality and reliability of the results of this survey.");
                    survey.setShortObjectives("All about " + id);
                    survey.setCommunication("Communication around " + id);
                    survey.setSpecimenUrl("http://specimenUrl/" + id);
                    survey.setDiffusionUrl("http://diffusion/" + id);
                    survey.setCnisUrl("http://cnis/" + id);
                    survey.setNoticeUrl("http://notice/" + id);
                    survey.setVisaNumber(year + RandomStringUtils.randomAlphanumeric(6).toUpperCase());
                    survey.setLongWording("Survey " + sourceName + " " + (year - j));
                    survey.setShortWording(id);
                    survey.setSampleSize(Integer.parseInt(RandomStringUtils.randomNumeric(5)));
                    setSurveys.add(survey);
                    surveyRepository.save(survey);
                    Set<Campaign> setCampaigns = new HashSet<>();

                    for (int k = 0; k < 4; k++) {
                        Campaign campaign = new Campaign();
                        int trimester = k + 1;
                        String period = "T0" + trimester;
                        campaign.setYear(year - j);
                        campaign.setPeriod(PeriodEnum.valueOf(period));
                        campaign.setId(sourceName + (year - j) + period);
                        campaign.setCampaignWording(
                                "Campaign about " + sourceName + " in " + (year - j) + " and period " + period);
                        setCampaigns.add(campaign);
                        campaignRepository.save(campaign);
                        Set<Partitioning> setParts = new HashSet<>();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");

                        for (int l = 0; l < 2; l++) {

                            Partitioning part = new Partitioning();
                            part.setId(sourceName + (year - j) + "T" + trimester + "00" + l);
                            log.info("Part created : {}", part.getId());
                            Date openingDate = sdf.parse("01/01/" + year);
                            Date closingDate = sdf.parse("31/12/" + year);
                            Date returnDate = sdf.parse("01/06/" + year);

                            part.setOpeningDate(openingDate);
                            part.setClosingDate(closingDate);
                            part.setReturnDate(returnDate);
                            setParts.add(part);
                            part.setCampaign(campaign);
                            partitioningRepository.save(part);
                        }
                        campaign.setSurvey(survey);
                        campaign.setPartitionings(setParts);
                        campaignRepository.save(campaign);

                    }
                    survey.setSource(source);
                    survey.setCampaigns(setCampaigns);
                    surveyRepository.save(survey);
                }
                source.setSurveys(setSurveys);
                source.setOwner(ownerInsee);
                sourceRepository.save(source);
                log.info("Source created : " + source);
                ownerInsee.setSources(setSourcesInsee);
                ownerRepository.saveAll(Arrays.asList(ownerInsee));
            }

        }

    }

    private void initQuestioning(Faker faker) {

        Long nbExistingQuestionings = questioningRepository.count();
        int year = 2023;

        Questioning qu;
        QuestioningEvent qe;
        Set<Questioning> setQuestioning;
        QuestioningAccreditation accreditation;
        Set<QuestioningAccreditation> questioningAccreditations;
        String fakeSiren;

        for (Long i = surveyUnitRepository.count(); i < 10; i++) {
            SurveyUnit su = new SurveyUnit();
            fakeSiren = "10000000" + i;
            su.setIdSu(fakeSiren);
            su.setIdentificationName("company name " + i);
            su.setIdentificationCode("CODE - 00000000" + i);
            surveyUnitRepository.save(su);

        }
        for (Long i = nbExistingQuestionings; i < 10; i++) {
            qu = new Questioning();
            List<QuestioningEvent> qeList = new ArrayList<>();
            questioningAccreditations = new HashSet<>();

            setQuestioning = new HashSet<>();
            qu.setModelName("m" + i);
            qu.setIdPartitioning("SOURCE" + (i % 2 + 1) + (year - i % 2) + "T" + (i % 4 + 1) + "00" + i % 2);
            questioningRepository.save(qu);
            SurveyUnit su = surveyUnitRepository.findById("10000000" + i).orElse(null);
            setQuestioning.add(qu);
            su.setQuestionings(setQuestioning);
            surveyUnitRepository.save(su);
            qu.setSurveyUnit(su);
            questioningRepository.save(qu);

            // questioning events
            // everybody in INITLA
            Optional<Partitioning> part = partitioningRepository.findById(qu.getIdPartitioning());

            qeList.add(new QuestioningEvent(
                    faker.date().between(part.get().getOpeningDate(), part.get().getClosingDate()),
                    TypeQuestioningEvent.INITLA, qu));
            qeList.add(new QuestioningEvent(
                    faker.date().between(part.get().getOpeningDate(), part.get().getClosingDate()),
                    TypeQuestioningEvent.PARTIELINT, qu));
            qeList.add(new QuestioningEvent(
                    faker.date().between(part.get().getOpeningDate(), part.get().getClosingDate()),
                    TypeQuestioningEvent.VALINT, qu));

            qeList.stream().forEach(questEvent -> questioningEventRepository.save(questEvent));

            for (int j = 0; j < 4; j++) {
                accreditation = new QuestioningAccreditation();
                accreditation.setIdContact("CONT" + (j + 1));
                accreditation.setQuestioning(qu);
                if (j == 0) {
                    accreditation.setMain(true);
                }
                questioningAccreditations.add(accreditation);
                questioningAccreditationRepository.save(accreditation);
            }
            qu.setQuestioningEvents(new HashSet<>(qeList));
            qu.setQuestioningAccreditations(questioningAccreditations);
            questioningRepository.save(qu);
            log.info("Questioning created : {}", qu);

        }
    }

    private void initView() {
        if (viewRepository.count() == 0) {

            List<QuestioningAccreditation> listAccreditations = questioningAccreditationRepository.findAll();
            listAccreditations.stream().forEach(a -> {
                Partitioning p = partitioningRepository.findById(a.getQuestioning().getIdPartitioning()).orElse(null);
                View view = new View();
                view.setIdentifier(contactRepository.findById(a.getIdContact()).orElse(null).getIdentifier());
                view.setCampaignId(p.getCampaign().getId());
                view.setIdSu(a.getQuestioning().getSurveyUnit().getIdSu());
                viewRepository.save(view);
            });

            Iterable<Contact> listContacts = contactRepository.findAll();
            for (Contact contact : listContacts) {
                if (viewRepository.findByIdentifier(contact.getIdentifier()).isEmpty()) {
                    View view = new View();
                    view.setIdentifier(contact.getIdentifier());
                    viewRepository.save(view);

                }
            }
        }
    }

}
