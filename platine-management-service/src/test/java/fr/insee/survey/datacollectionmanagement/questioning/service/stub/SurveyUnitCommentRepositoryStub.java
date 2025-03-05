package fr.insee.survey.datacollectionmanagement.questioning.service.stub;

import fr.insee.survey.datacollectionmanagement.questioning.domain.SurveyUnitComment;
import fr.insee.survey.datacollectionmanagement.questioning.repository.SurveyUnitCommentRepository;
import lombok.Setter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Setter
public class SurveyUnitCommentRepositoryStub implements SurveyUnitCommentRepository {


	@Override
	public void flush() {
		// stub
	}

	@Override
	public <S extends SurveyUnitComment> S saveAndFlush(S entity) {
		return null;
	}

	@Override
	public <S extends SurveyUnitComment> List<S> saveAllAndFlush(Iterable<S> entities) {
		return List.of();
	}

	@Override
	public void deleteAllInBatch(Iterable<SurveyUnitComment> entities) {
		// stub
	}

	@Override
	public void deleteAllByIdInBatch(Iterable<Long> longs) {
		// stub
	}

	@Override
	public void deleteAllInBatch() {
		// stub
	}

	@Override
	public SurveyUnitComment getOne(Long aLong) {
		return null;
	}

	@Override
	public SurveyUnitComment getById(Long aLong) {
		return null;
	}

	@Override
	public SurveyUnitComment getReferenceById(Long aLong) {
		return null;
	}

	@Override
	public <S extends SurveyUnitComment> Optional<S> findOne(Example<S> example) {
		return Optional.empty();
	}

	@Override
	public <S extends SurveyUnitComment> List<S> findAll(Example<S> example) {
		return List.of();
	}

	@Override
	public <S extends SurveyUnitComment> List<S> findAll(Example<S> example, Sort sort) {
		return List.of();
	}

	@Override
	public <S extends SurveyUnitComment> Page<S> findAll(Example<S> example, Pageable pageable) {
		return null;
	}

	@Override
	public <S extends SurveyUnitComment> long count(Example<S> example) {
		return 0;
	}

	@Override
	public <S extends SurveyUnitComment> boolean exists(Example<S> example) {
		return false;
	}

	@Override
	public <S extends SurveyUnitComment, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		return null;
	}

	@Override
	public <S extends SurveyUnitComment> S save(S entity) {
		return null;
	}

	@Override
	public <S extends SurveyUnitComment> List<S> saveAll(Iterable<S> entities) {
		return List.of();
	}

	@Override
	public Optional<SurveyUnitComment> findById(Long aLong) {
		return Optional.empty();
	}

	@Override
	public boolean existsById(Long aLong) {
		return false;
	}

	@Override
	public List<SurveyUnitComment> findAll() {
		return List.of();
	}

	@Override
	public List<SurveyUnitComment> findAllById(Iterable<Long> longs) {
		return List.of();
	}

	@Override
	public long count() {
		return 0;
	}

	@Override
	public void deleteById(Long aLong) {
		// stub
	}

	@Override
	public void delete(SurveyUnitComment entity) {
		// stub
	}

	@Override
	public void deleteAllById(Iterable<? extends Long> longs) {
		// stub
	}

	@Override
	public void deleteAll(Iterable<? extends SurveyUnitComment> entities) {
		// stub
	}

	@Override
	public void deleteAll() {
		// stub
	}

	@Override
	public List<SurveyUnitComment> findAll(Sort sort) {
		return List.of();
	}

	@Override
	public Page<SurveyUnitComment> findAll(Pageable pageable) {
		return null;
	}
}
