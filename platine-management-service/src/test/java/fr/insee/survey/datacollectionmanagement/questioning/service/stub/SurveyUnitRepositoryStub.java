package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnit;
import fr.insee.survey.datacollectionmanagement.questioning.dto.SearchSurveyUnitDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitRepository;
import lombok.Setter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class SurveyUnitRepositoryStub implements SurveyUnitRepository {

	@Setter
	private boolean shouldThrow = false;

	@Setter
	private SurveyUnit surveyUnit;

	@Override
	public List<SurveyUnit> findAllByIdentificationCode(String identificationCode) {
		return List.of();
	}

	@Override
	public Page<SearchSurveyUnitDto> findByIdentifier(String param, Pageable pageable) {
		return null;
	}

	@Override
	public Page<SearchSurveyUnitDto> findByIdentificationCode(String param, Pageable pageable) {
		return null;
	}

	@Override
	public Page<SearchSurveyUnitDto> findByIdentificationName(String param, Pageable pageable) {
		return null;
	}

	@Override
	public void flush() {

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

	}

	@Override
	public void deleteAllByIdInBatch(Iterable<String> strings) {

	}

	@Override
	public void deleteAllInBatch() {

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

	}

	@Override
	public void delete(SurveyUnit entity) {

	}

	@Override
	public void deleteAllById(Iterable<? extends String> strings) {

	}

	@Override
	public void deleteAll(Iterable<? extends SurveyUnit> entities) {

	}

	@Override
	public void deleteAll() {

	}

	@Override
	public List<SurveyUnit> findAll(Sort sort) {
		return List.of();
	}

	@Override
	public Page<SurveyUnit> findAll(Pageable pageable) {
		return null;
	}
}
