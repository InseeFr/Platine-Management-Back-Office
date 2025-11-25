package fr.insee.survey.datacollectionmanagement.questioning.validation;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SortValidatorTest {

    private final SortValidator validator = new SortValidator();

    @Test
    void sanitizeSort_withAllowedProperties_keepsAllOrdersInSameDirectionAndOrder() {
        // given
        Sort input = Sort.by(
                Sort.Order.asc("priority"),
                Sort.Order.desc("score")
        );

        // when
        Sort result = validator.sanitizeSort(input);

        // then
        List<Sort.Order> orders = result.toList();
        assertThat(orders).containsExactly(
                new Sort.Order(Sort.Direction.ASC, "priority"),
                new Sort.Order(Sort.Direction.DESC, "score")
        );
    }

    @Test
    void sanitizeSort_withOnlyDisallowedProperties_returnsUnsorted() {
        // given
        Sort input = Sort.by(
                Sort.Order.asc("unknown"),
                Sort.Order.desc("other")
        );

        // when
        Sort result = validator.sanitizeSort(input);

        // then
        assertThat(result.isSorted()).isFalse();
        assertThat(result.toList()).isEmpty();
    }

    @Test
    void sanitizeSort_withMixedAllowedAndDisallowed_keepsOnlyAllowedInOriginalOrder() {
        // given
        Sort input = Sort.by(
                Sort.Order.asc("priority"),
                Sort.Order.desc("unknown"),
                Sort.Order.asc("score")
        );

        // when
        Sort result = validator.sanitizeSort(input);

        // then
        List<Sort.Order> orders = result.toList();
        assertThat(orders).containsExactly(
                new Sort.Order(Sort.Direction.ASC, "priority"),
                new Sort.Order(Sort.Direction.ASC, "score")
        );
    }

    @Test
    void sanitizeSort_withUnsortedInput_returnsUnsorted() {
        // given
        Sort input = Sort.unsorted();

        // when
        Sort result = validator.sanitizeSort(input);

        // then
        assertThat(result.isSorted()).isFalse();
        assertThat(result.toList()).isEmpty();
    }
}