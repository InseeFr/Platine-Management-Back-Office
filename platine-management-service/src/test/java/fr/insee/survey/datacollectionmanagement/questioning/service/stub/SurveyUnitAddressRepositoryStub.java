package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnitAddress;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitAddressRepository;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class SurveyUnitAddressRepositoryStub implements SurveyUnitAddressRepository {


	@Override
	public void flush() {
		//not used
	}

	@Override
	public <S extends SurveyUnitAddress> S saveAndFlush(S entity) {
		return null;
	}

	@Override
	public <S extends SurveyUnitAddress> List<S> saveAllAndFlush(Iterable<S> entities) {
		return List.of();
	}

	@Override
	public void deleteAllInBatch(Iterable<SurveyUnitAddress> entities) {
		//not used
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> longs) {
		//not used
	}

	@Override
	public void deleteAllInBatch() {
		//not used
	}

	@Override
	public SurveyUnitAddress getOne(Long aLong) {
		return null;
	}

	@Override
	public SurveyUnitAddress getById(Long aLong) {
		return null;
	}

	@Override
	public SurveyUnitAddress getReferenceById(Long aLong) {
		return null;
	}

	@Override
	public <S extends SurveyUnitAddress> Optional<S> findOne(Example<S> example) {
		return Optional.empty();
	}

	@Override
	public <S extends SurveyUnitAddress> List<S> findAll(Example<S> example) {
		return List.of();
	}

	@Override
	public <S extends SurveyUnitAddress> List<S> findAll(Example<S> example, Sort sort) {
		return List.of();
	}

	@Override
	public <S extends SurveyUnitAddress> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	@Override
	public <S extends SurveyUnitAddress> long count(Example<S> example) {
		return 0;
	}

	@Override
	public <S extends SurveyUnitAddress> boolean exists(Example<S> example) {
		return false;
	}

	@Override
	public <S extends SurveyUnitAddress, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>,
			R> queryFunction) {
		return null;
	}

	@Override
	public <S extends SurveyUnitAddress> S save(S entity) {
		return entity;
	}

	@Override
	public <S extends SurveyUnitAddress> List<S> saveAll(Iterable<S> entities) {
		return List.of();
	}

	@Override
	public Optional<SurveyUnitAddress> findById(Long aLong) {
		return Optional.empty();
	}

	@Override
	public boolean existsById(Long aLong) {
		return false;
	}

	@Override
	public List<SurveyUnitAddress> findAll() {
		return List.of();
	}

	@Override
	public List<SurveyUnitAddress> findAllById(Iterable<Long> longs) {
		return List.of();
	}

	@Override
	public long count() {
		return 0;
	}

	@Override
	public void deleteById(Long aLong) {
		//not used
	}

	@Override
	public void delete(SurveyUnitAddress entity) {
		//not used
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> longs) {
		//not used
	}

	@Override
	public void deleteAll(Iterable<? extends SurveyUnitAddress> entities) {
		//not used
	}

	@Override
	public void deleteAll() {
		//not used
	}

	@Override
	public List<SurveyUnitAddress> findAll(Sort sort) {
		return List.of();
	}

	@Override
	public Page<SurveyUnitAddress> findAll(Pageable pageable) {
		return null;
	}
}
