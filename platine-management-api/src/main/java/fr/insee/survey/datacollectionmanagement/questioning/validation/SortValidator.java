package fr.insee.survey.datacollectionmanagement.questioning.validation;


import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;


@Component
public class SortValidator {
    private static final Set<String> ALLOWED_SORT_PROPERTIES =
            Set.of("priority", "score");


    public Sort sanitizeSort(Sort sort) {
        List<Sort.Order> safeOrders = sort.stream()
                .filter(order -> ALLOWED_SORT_PROPERTIES.contains(order.getProperty()))
                .toList();

        return safeOrders.isEmpty() ? Sort.unsorted() : Sort.by(safeOrders);
    }
}
