package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.ContactAccreditedToSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;

import java.util.Collection;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Setter
public class SurveyUnitRepositoryStub implements SurveyUnitRepository {

	private boolean shouldThrow = false;
	@Getter
	private SurveyUnit surveyUnit;
	private List<SearchSurveyUnitDto> echoes;
	private List<SurveyUnit> surveyUnits;
	private List<ContactAccreditedToSurveyUnitDto> listContactAccreditedToSurveyUnitDto;


	@Override
	public List<SurveyUnit> findAllByIdentificationCode(String identificationCode) {
		return List.of();
	}

	@Override
	public Page<SearchSurveyUnitDto> findByIdentifier(String param, Pageable pageable) {
		List<SearchSurveyUnitDto> searches = echoes.stream().filter(search -> param.equals(search.getIdSu())).toList();
		return new PageImpl<>(searches, pageable, searches.size());
	}

	@Override
	public Page<SearchSurveyUnitDto> findByIdentificationCode(String param, Pageable pageable) {
		List<SearchSurveyUnitDto> searches =
				echoes.stream().filter(search -> param.equals(search.getIdentificationCode())).toList();
		return new PageImpl<>(searches, pageable, searches.size());
	}

	@Override
	public Page<SearchSurveyUnitDto> findByIdentificationName(String param, Pageable pageable) {
		List<SearchSurveyUnitDto> searches =
				echoes.stream().filter(search -> param.equals(search.getIdentificationName())).toList();
		return new PageImpl<>(searches, pageable, searches.size());
	}

	@Override
	public Page<SearchSurveyUnitDto> findByParam(String param, Pageable pageable) {
		return null;
	}

	@Override
	public List<ContactAccreditedToSurveyUnitDto> findContactsAccreditedToSurveyUnit(String surveyUnitId) {
		return listContactAccreditedToSurveyUnitDto;
	}

	@Override
	public List<String> findCampaignIdsBySurveyUnitId(String surveyUnitId) {
		return List.of();
	}

    @Override
    public Set<String> findExistingSurveyUnitIds(Collection<String> ids) {
        return Set.of();
    }

    @Override
    public List<SurveyUnit> findAllByIdSuIn(Collection<String> ids) {
        return List.of();
    }


    @Override
	public void flush() {
		//not used
	}

	@Override
	public <S extends SurveyUnit> S saveAndFlush(S entity) {
		return null;
	}

	@Override
	public <S extends SurveyUnit> List<S> saveAllAndFlush(Iterable<S> entities) {
		return List.of();
	}

	@Override
	public void deleteAllInBatch(Iterable<SurveyUnit> entities) {
		//not used
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<String> strings) {
		//not used
	}

	@Override
	public void deleteAllInBatch() {
		//not used
	}

	@Override
	public SurveyUnit getOne(String s) {
		return null;
	}

	@Override
	public SurveyUnit getById(String s) {
		return null;
	}

	@Override
	public SurveyUnit getReferenceById(String s) {
		return null;
	}

	@Override
	public <S extends SurveyUnit> Optional<S> findOne(Example<S> example) {
		return Optional.empty();
	}

	@Override
	public <S extends SurveyUnit> List<S> findAll(Example<S> example) {
		return List.of();
	}

	@Override
	public <S extends SurveyUnit> List<S> findAll(Example<S> example, Sort sort) {
		return List.of();
	}

	@Override
	public <S extends SurveyUnit> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	@Override
	public <S extends SurveyUnit> long count(Example<S> example) {
		return 0;
	}

	@Override
	public <S extends SurveyUnit> boolean exists(Example<S> example) {
		return false;
	}

	@Override
	public <S extends SurveyUnit, R> R findBy(Example<S> example,
											  Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		return null;
	}

	@Override
	public <S extends SurveyUnit> S save(S entity) {
		return entity;
	}

	@Override
	public <S extends SurveyUnit> List<S> saveAll(Iterable<S> entities) {
		return List.of();
	}

	@Override
	public Optional<SurveyUnit> findById(String s) {

		if (shouldThrow) {

			return Optional.empty();
		}
		return Optional.of(surveyUnit);

	}

	@Override
	public boolean existsById(String s) {
		return false;
	}

	@Override
	public List<SurveyUnit> findAll() {
		return List.of();
	}

	@Override
	public List<SurveyUnit> findAllById(Iterable<String> strings) {
		return List.of();
	}

	@Override
	public long count() {
		return 0;
	}

	@Override
	public void deleteById(String s) {
		if (s.equals(surveyUnit.getIdSu())) {
			surveyUnit = null;
		}
	}

	@Override
	public void delete(SurveyUnit entity) {
		//not used
	}


	@Override
	public void deleteAllById(Iterable<? extends String> strings) {
		//not used
	}

	@Override
	public void deleteAll(Iterable<? extends SurveyUnit> entities) {
		//not used
	}

	@Override
	public void deleteAll() {
		//not used
	}

	@Override
	public List<SurveyUnit> findAll(Sort sort) {
		return List.of();
	}

	@Override
	public Page<SurveyUnit> findAll(Pageable pageable) {
		List<SurveyUnit> pagedSurveyUnits = surveyUnits.stream().limit(pageable.getPageSize()).toList();
		return new PageImpl<>(pagedSurveyUnits, pageable, pageable.getPageSize());
	}
}
