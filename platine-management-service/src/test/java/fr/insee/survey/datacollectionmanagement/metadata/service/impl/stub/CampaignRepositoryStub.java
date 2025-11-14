package fr.insee.survey.datacollectionmanagement.metadata.service.impl.stub;

import fr.insee.survey.datacollectionmanagement.metadata.domain.Campaign;
import fr.insee.survey.datacollectionmanagement.metadata.enums.DataCollectionEnum;
import fr.insee.survey.datacollectionmanagement.metadata.repository.CampaignRepository;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class CampaignRepositoryStub implements CampaignRepository {

    @Setter
    private List<Campaign> campaigns;

    @Override
    public List<Campaign> findByPeriod(String period) {
        return List.of();
    }

    @Override
    public List<Campaign> findBySourceYearPeriod(String source, Integer year, String period) {
        return List.of();
    }

    @Override
    public List<Campaign> findBySourcePeriod(String source, String period) {
        return List.of();
    }

    @Override
    public Page<Campaign> findBySource(String source, Pageable pageable) {

        if (campaigns == null || campaigns.isEmpty()) {
            return Page.empty();
        }

        List<Campaign> filtered = StringUtils.isBlank(source) ? campaigns : campaigns.stream().filter(c -> c.getSurvey().getSource().getId().equalsIgnoreCase(source)).toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<Campaign> pagedList = filtered.subList(start, end);

        return new PageImpl<>(pagedList, pageable, filtered.size());
    }

    @Override
    public List<Campaign> findByDataCollectionTargetIsNot(DataCollectionEnum dataCollectionTarget) {
        return campaigns.stream().filter(c -> c.getDataCollectionTarget() != dataCollectionTarget).toList();
    }

    @Override
    public List<Campaign> findOpenedCampaigns(Instant now) {
        return List.of();
    }

    @Override
    public List<Campaign> findOpenedCampaignsForUser(String userId, Instant instant) {
        return List.of();
    }

    @Override
    public List<Campaign> findOpenedCampaignsForUserGroups(String userId, Instant instant) {
        return List.of();
    }

    @Override
    public void flush() {
        // stub
    }

    @Override
    public <S extends Campaign> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Campaign> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Campaign> entities) {
// stub
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<String> strings) {
        // stub
    }

    @Override
    public void deleteAllInBatch() {
        // stub
    }

    @Override
    public Campaign getOne(String s) {
        return null;
    }

    @Override
    public Campaign getById(String s) {
        return null;
    }

    @Override
    public Campaign getReferenceById(String s) {
        return null;
    }

    @Override
    public <S extends Campaign> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Campaign> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Campaign> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Campaign> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Campaign> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Campaign> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Campaign, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public <S extends Campaign> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Campaign> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public Optional<Campaign> findById(String s) {
        if (StringUtils.isBlank(s)) {
            return Optional.empty();
        }
        return campaigns.stream().filter(c -> s.equals(c.getId())).findFirst();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }

    @Override
    public List<Campaign> findAll() {
        return campaigns;
    }

    @Override
    public List<Campaign> findAllById(Iterable<String> strings) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(String s) {
        // stub
    }

    @Override
    public void delete(Campaign entity) {
        // stub
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        // stub
    }

    @Override
    public void deleteAll(Iterable<? extends Campaign> entities) {
        // stub
    }

    @Override
    public void deleteAll() {
        // stub
    }

    @Override
    public List<Campaign> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Campaign> findAll(Pageable pageable) {
        return null;
    }
}
