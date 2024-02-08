package fr.insee.survey.datacollectionmanagement.dataloader;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import fr.insee.survey.datacollectionmanagement.contact.domain.Address;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact;
import fr.insee.survey.datacollectionmanagement.contact.domain.Contact.Gender;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent;
import fr.insee.survey.datacollectionmanagement.contact.domain.ContactEvent.ContactEventType;
import fr.insee.survey.datacollectionmanagement.contact.repository.AddressRepository;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactEventRepository;
import fr.insee.survey.datacollectionmanagement.contact.repository.ContactRepository;
import fr.insee.survey.datacollectionmanagement.metadata.domain.*;
import fr.insee.survey.datacollectionmanagement.metadata.repository.*;
import fr.insee.survey.datacollectionmanagement.metadata.util.PeriodEnum;
import fr.insee.survey.datacollectionmanagement.metadata.util.PeriodicityEnum;
import fr.insee.survey.datacollectionmanagement.questioning.domain.*;
import fr.insee.survey.datacollectionmanagement.questioning.repository.*;
import fr.insee.survey.datacollectionmanagement.questioning.util.TypeQuestioningEvent;
import fr.insee.survey.datacollectionmanagement.view.domain.View;
import fr.insee.survey.datacollectionmanagement.view.repository.ViewRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
@Profile("poc")
@Slf4j
@RequiredArgsConstructor
public class DataloaderPoc {

    private final ContactRepository contactRepository;

    private final AddressRepository addressRepository;

    private final ContactEventRepository contactEventRepository;

    private final OwnerRepository ownerRepository;

    private final SupportRepository supportRepository;

    private final SourceRepository sourceRepository;

    private final SurveyRepository surveyRepository;

    private final SurveyUnitRepository surveyUnitRepository;

    private final SurveyUnitAddressRepository surveyUnitAddressRepository;

    private final OperatorServiceRepository operatorServiceRepository;

    private final OperatorRepository operatorRepository;

    private final QuestioningRepository questioningRepository;

    private final QuestioningAccreditationRepository questioningAccreditationRepository;

    private final CampaignRepository campaignRepository;

    private final PartitioningRepository partitioningRepository;

    private final EventOrderRepository orderRepository;

    private final QuestioningEventRepository questioningEventRepository;

    private final ViewRepository viewRepository;

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    static final String NUMBERS = "0123456789";

    static SecureRandom rnd = new SecureRandom();

    @PostConstruct
    public void init() {

        Faker faker = new Faker();

        initOrder();
        initContact(faker);
        initMetadata(faker);
        initQuestionning(faker);
        initView();
        initSurveyUnitAddressAndOperators(faker);

    }

    private void initSurveyUnitAddressAndOperators(Faker faker) {
        if (surveyUnitAddressRepository.count() == 0) {
            for (SurveyUnit su : surveyUnitRepository.findAll()) {
                SurveyUnitAddress a = new SurveyUnitAddress();
                com.github.javafaker.Address fakeAddress = faker.address();

                a.setCountryName(fakeAddress.country());
                a.setStreetNumber(fakeAddress.buildingNumber());
                a.setStreetName(fakeAddress.streetName());
                a.setZipCode(fakeAddress.zipCode());
                a.setCityName(fakeAddress.cityName());
                su.setSurveyUnitAddress(a);
                surveyUnitRepository.save(su);
            }
        }

        if (operatorServiceRepository.count() == 0) {
            Set<Operator> setOpConj = new HashSet<>();
            OperatorService operatorServiceConj = new OperatorService();
            operatorServiceConj.setName("Conjoncture");
            operatorServiceConj.setMail("conjoncture@Cocorico.fr");
            for (int i = 0; i < 9; i++) {
                setOpConj.add(createOperator(faker));
            }
            operatorServiceConj.setOperators(setOpConj);
            operatorServiceRepository.save(operatorServiceConj);

            Set<Operator> setOpLogement = new HashSet<>();
            OperatorService operatorServiceLogement = new OperatorService();
            operatorServiceLogement.setName("Logement");
            operatorServiceLogement.setMail("logement@Cocorico.fr");
            operatorServiceRepository.save(operatorServiceLogement);
            for (int i = 0; i < 9; i++) {
                setOpLogement.add(createOperator(faker));
            }
            operatorServiceLogement.setOperators(setOpLogement);
            operatorServiceRepository.save(operatorServiceLogement);

            Set<Operator> setOpEmploi = new HashSet<>();
            OperatorService operatorServiceEmploi = new OperatorService();
            operatorServiceEmploi.setName("Emploi");
            operatorServiceEmploi.setMail("emploi@Cocorico.fr");
            operatorServiceRepository.save(operatorServiceEmploi);
            for (int i = 0; i < 9; i++) {
                setOpEmploi.add(createOperator(faker));
            }
            operatorServiceEmploi.setOperators(setOpEmploi);
            operatorServiceRepository.save(operatorServiceEmploi);

            Set<Operator> setOpPrix = new HashSet<>();
            OperatorService operatorServicePrix = new OperatorService();
            operatorServicePrix.setName("Prix");
            operatorServicePrix.setMail("prix@Cocorico.fr");
            operatorServiceRepository.save(operatorServicePrix);
            for (int i = 0; i < 9; i++) {
                setOpPrix.add(createOperator(faker));
            }
            operatorServicePrix.setOperators(setOpPrix);
            operatorServiceRepository.save(operatorServicePrix);
        }

        if (operatorServiceRepository.count() == 0) {
            for (SurveyUnit su : surveyUnitRepository.findAll()) {
                SurveyUnitAddress a = new SurveyUnitAddress();
                com.github.javafaker.Address fakeAddress = faker.address();

                a.setCountryName(fakeAddress.country());
                a.setStreetNumber(fakeAddress.buildingNumber());
                a.setStreetName(fakeAddress.streetName());
                a.setZipCode(fakeAddress.zipCode());
                a.setCityName(fakeAddress.cityName());
                su.setSurveyUnitAddress(a);
                surveyUnitRepository.save(su);
            }
        }

    }

    private Operator createOperator(Faker faker) {
        Operator operator = new Operator();
        Name n = faker.name();
        String name = n.lastName();
        String firstName = n.firstName();
        operator.setLastName(name);
        operator.setFirstName(firstName);
        operator.setPhoneNumber(faker.phoneNumber().phoneNumber());
        return operatorRepository.save(operator);
    }

    private void initOrder() {

        Long nbExistingOrders = orderRepository.count();
        log.info("{} orders in database", nbExistingOrders);

        if (nbExistingOrders == 0) {
            // Creating table order
            log.info("loading eventorder data");
            orderRepository
                    .saveAndFlush(new EventOrder(Long.parseLong("8"), TypeQuestioningEvent.REFUSAL.toString(), 8));
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

    private void initContact(Faker faker) {

        List<Contact> listContact = new ArrayList<>();
        List<Address> listAddresses = new ArrayList<>();
        Long nbExistingContacts = contactRepository.count();

        log.info("{} contacts exist in database", nbExistingContacts);

        int nbContacts = 1000000;

        for (Long i = nbExistingContacts; i < nbContacts; i++) {
            long start = System.currentTimeMillis();

            final Contact c = new Contact();
            final Address a = new Address();

            Name n = faker.name();
            String name = n.lastName();
            String firstName = n.firstName();
            com.github.javafaker.Address fakeAddress = faker.address();

            a.setCountryName(fakeAddress.country());
            a.setStreetNumber(fakeAddress.buildingNumber());
            a.setStreetName(fakeAddress.streetName());
            a.setZipCode(fakeAddress.zipCode());
            a.setCityName(fakeAddress.cityName());
            // addressRepository.save(a);
            listAddresses.add(a);

            c.setIdentifier(randomString(7).toUpperCase());
            c.setLastName(name);
            c.setFirstName(firstName);
            c.setPhone(faker.phoneNumber().phoneNumber());
            c.setGender(Gender.valueOf(faker.demographic().sex()));
            c.setFunction(faker.job().title());
            c.setComment(faker.beer().name());
            c.setEmail(firstName.toLowerCase() + "." + name.toLowerCase() + "@cocorico.fr");
            c.setAddress(a);
            listContact.add(c);

            if ((i + 1) % 10000 == 0) {
                addressRepository.saveAll(listAddresses);
                contactRepository.saveAll(listContact);
                listAddresses = new ArrayList<>();
                listContact = new ArrayList<>();
                long end = System.currentTimeMillis();

                log.info("It took {}ms to execute saveAll() for 10000 contacts.", (end - start));
            }

        }

        Long nbContactEvents = contactEventRepository.count();

        for (Long j = nbContactEvents; j < 300; j++) {
            Contact contact = contactRepository.findRandomContact();
            ContactEvent contactEvent = new ContactEvent();
            contactEvent.setType(ContactEventType.create);
            contactEvent.setEventDate(new Date());
            contactEvent.setContact(contact);
            contactEventRepository.save(contactEvent);
            Set<ContactEvent> setContactEvents = new HashSet<>();
            setContactEvents.add(contactEvent);
            contact.setContactEvents(setContactEvents);
            contactRepository.save(contact);
        }


    }

    private void initMetadata(Faker faker) {

        int year = 2023;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        Date dateEndOfYear = calendar.getTime();

        Owner ownerInsee = new Owner();
        ownerInsee.setId("insee");
        ownerInsee.setLabel("Insee");
        Set<Source> setSourcesInsee = new HashSet<>();

        Owner ownerAgri = new Owner();
        ownerAgri.setId("agri");
        ownerAgri.setLabel("SSM Agriculture");
        Set<Source> setSourcesSsp = new HashSet<>();

        Support supportInseeHdf = new Support();
        supportInseeHdf.setId("inseehdf");
        supportInseeHdf.setLabel("Insee Hauts-de-France");
        Set<Source> setSourcesSupportInsee = new HashSet<>();

        Support supportSsne = new Support();
        supportSsne.setId("ssne");
        supportSsne.setLabel("Insee Normandie - SSNE");
        Set<Source> setSourcesSupportSsne = new HashSet<>();

        log.info("{} campaigns exist in database", campaignRepository.count());

        while (sourceRepository.count() < 10) {

            Source source = new Source();

            String nameSource = "LOAD" + sourceRepository.count() + 'A';

            if (!StringUtils.contains(nameSource, " ") && sourceRepository.findById(nameSource).isEmpty()) {

                source.setId(nameSource);
                source.setLongWording("Have you ever heard about " + nameSource + " ?");
                source.setShortWording("Source about " + nameSource);
                source.setPeriodicity(PeriodicityEnum.M);
                sourceRepository.save(source);
                Set<Survey> setSurveys = new HashSet<>();
                Integer i = rnd.nextInt();
                if (i % 2 == 0)
                    setSourcesInsee.add(source);
                else {
                    setSourcesSsp.add(source);
                }

                for (int j = 0; j < 4; j++) {

                    Survey survey = new Survey();
                    String id = nameSource + (year - j);
                    survey.setId(id);
                    survey.setYear(year - j);
                    survey.setLongObjectives("The purpose of this survey is to find out everything you can about "
                            + nameSource
                            + ". Your response is essential to ensure the quality and reliability of the results of this survey.");
                    survey.setShortObjectives("All about " + id);
                    survey.setCommunication("Communication around " + id);
                    survey.setSpecimenUrl("http://specimenUrl/" + id);
                    survey.setDiffusionUrl("http://diffusion/" + id);
                    survey.setCnisUrl("http://cnis/" + id);
                    survey.setNoticeUrl("http://notice/" + id);
                    survey.setVisaNumber(year + randomString(6).toUpperCase());
                    survey.setLongWording("Survey " + nameSource + " " + (year - j));
                    survey.setShortWording(id);
                    survey.setSampleSize(Integer.parseInt(randomNumeric(5)));
                    setSurveys.add(survey);
                    surveyRepository.save(survey);
                    Set<Campaign> setCampaigns = new HashSet<>();

                    for (int k = 0; k < 12; k++) {
                        Campaign campaign = new Campaign();
                        int month = k + 1;
                        String period = month < 10 ? "M0" + month : "M" + month;
                        campaign.setYear(year - j);
                        campaign.setPeriod(PeriodEnum.valueOf(period));
                        campaign.setId(nameSource + (year - j) + period);
                        campaign.setCampaignWording(
                                "Campaign about " + nameSource + " in " + (year - j) + " and period " + period);
                        setCampaigns.add(campaign);
                        campaignRepository.save(campaign);
                        Set<Partitioning> setParts = new HashSet<>();

                        for (int l = 0; l < 3; l++) {

                            Partitioning part = new Partitioning();
                            part.setId(nameSource + (year - j) + "M" + month + "-00" + l);
                            Date openingDate = faker.date().past(90, 0, TimeUnit.DAYS);
                            Date closingDate = faker.date().between(openingDate, dateEndOfYear);
                            Date returnDate = faker.date().between(openingDate, closingDate);

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
                sourceRepository.save(source);
                ownerInsee.setSources(setSourcesInsee);
                ownerAgri.setSources(setSourcesSsp);
                ownerRepository.saveAll(Arrays.asList(ownerInsee, ownerAgri));

                supportInseeHdf.setSources(setSourcesSupportInsee);
                supportSsne.setSources(setSourcesSupportSsne);
                supportRepository.saveAll(Arrays.asList(supportInseeHdf, supportSsne));
            }

        }

    }

    private void initQuestionning(Faker faker) {

        Long nbExistingQuestionings = questioningRepository.count();

        log.info("{} questionings exist in database", nbExistingQuestionings);

        long start = System.currentTimeMillis();
        Questioning qu;
        QuestioningEvent qe;
        Set<Questioning> setQuestioning;
        QuestioningAccreditation accreditation;
        Set<QuestioningAccreditation> questioningAccreditations;
        String fakeSiren;

        log.info("{} survey units exist in database", surveyUnitRepository.count());

        for (Long i = surveyUnitRepository.count(); i < 500000; i++) {
            SurveyUnit su = new SurveyUnit();
            fakeSiren = randomNumeric(9);

            su.setIdSu(fakeSiren);
            su.setIdentificationName(faker.company().name());
            su.setIdentificationCode(fakeSiren);
            surveyUnitRepository.save(su);

        }
        for (Long i = nbExistingQuestionings; i < 500000; i++) {
            qu = new Questioning();
            qe = new QuestioningEvent();
            List<QuestioningEvent> qeList = new ArrayList<>();
            questioningAccreditations = new HashSet<>();

            setQuestioning = new HashSet<>();
            qu.setModelName("m" + randomNumeric(2));
            qu.setIdPartitioning(partitioningRepository.findRandomPartitioning().getId());
            SurveyUnit su = surveyUnitRepository.findRandomSurveyUnit();
            qu.setSurveyUnit(su);
            questioningRepository.save(qu);
            setQuestioning.add(qu);
            su.setQuestionings(setQuestioning);

            // questioning events
            // everybody in INITLA
            Optional<Partitioning> part = partitioningRepository.findById(qu.getIdPartitioning());
            Date eventDate = faker.date().between(part.get().getOpeningDate(), part.get().getClosingDate());
            qe.setType(TypeQuestioningEvent.INITLA);
            qe.setDate(eventDate);
            qe.setQuestioning(qu);
            qeList.add(qe);

            int qeProfile = rnd.nextInt(10);

            switch (qeProfile) {
                case 0:
                    qeList.add(new QuestioningEvent(
                            faker.date().between(part.get().getOpeningDate(), part.get().getClosingDate()),
                            TypeQuestioningEvent.REFUSAL, qu));
                    break;
                case 1:
                    qeList.add(new QuestioningEvent(
                            faker.date().between(part.get().getOpeningDate(), part.get().getClosingDate()),
                            TypeQuestioningEvent.PND, qu));
                    break;
                case 2:
                    qeList.add(new QuestioningEvent(
                            faker.date().between(part.get().getOpeningDate(), part.get().getClosingDate()),
                            TypeQuestioningEvent.FOLLOWUP, qu));
                    qeList.add(new QuestioningEvent(
                            faker.date().between(part.get().getOpeningDate(), part.get().getClosingDate()),
                            TypeQuestioningEvent.FOLLOWUP, qu));
                    qeList.add(new QuestioningEvent(
                            faker.date().between(part.get().getOpeningDate(), part.get().getClosingDate()),
                            TypeQuestioningEvent.PARTIELINT, qu));
                    break;
                case 3, 4:
                    qeList.add(new QuestioningEvent(
                            faker.date().between(part.get().getOpeningDate(), part.get().getClosingDate()),
                            TypeQuestioningEvent.FOLLOWUP, qu));
                    qeList.add(new QuestioningEvent(
                            faker.date().between(part.get().getOpeningDate(), part.get().getClosingDate()),
                            TypeQuestioningEvent.FOLLOWUP, qu));
                    qeList.add(new QuestioningEvent(
                            faker.date().between(part.get().getOpeningDate(), part.get().getClosingDate()),
                            TypeQuestioningEvent.VALINT, qu));
                    break;
                case 5, 6, 7:
                    qeList.add(new QuestioningEvent(
                            faker.date().between(part.get().getOpeningDate(), part.get().getClosingDate()),
                            TypeQuestioningEvent.PARTIELINT, qu));
                    qeList.add(new QuestioningEvent(
                            faker.date().between(part.get().getOpeningDate(), part.get().getClosingDate()),
                            TypeQuestioningEvent.VALINT, qu));
                    break;
                default:
                    qeList.add(new QuestioningEvent(
                            faker.date().between(part.get().getOpeningDate(), part.get().getClosingDate()),
                            TypeQuestioningEvent.PARTIELINT, qu));
                    break;

            }

            qeList.stream().forEach(questioningEventRepository::save);

            for (int j = 0; j < 4; j++) {
                accreditation = new QuestioningAccreditation();
                accreditation.setIdContact(contactRepository.findRandomIdentifierContact());
                accreditation.setQuestioning(qu);
                questioningAccreditations.add(accreditation);
                questioningAccreditationRepository.save(accreditation);
            }
            qu.setQuestioningAccreditations(questioningAccreditations);
            questioningRepository.save(qu);
            if (i % 100 == 0) {
                long end = System.currentTimeMillis();
                log.info("It took {}ms to execute save() for 100 questionings.", (end - start));
                start = System.currentTimeMillis();
            }

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


    String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    String randomNumeric(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(NUMBERS.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }


}