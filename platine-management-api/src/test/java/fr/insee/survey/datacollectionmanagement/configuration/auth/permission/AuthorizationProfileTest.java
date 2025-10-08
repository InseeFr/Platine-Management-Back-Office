package fr.insee.survey.datacollectionmanagement.configuration.auth.permission;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AuthorizationProfileTest {

    @Test
    @DisplayName("Should return true when sourceId exists in sources")
    void testCan_withExistingSourceId_shouldReturnTrue() {
        // Given
        Set<String> sources = Set.of("SOURCE_1", "SOURCE_2", "SOURCE_3");
        AuthorizationProfile profile = new AuthorizationProfile(null, sources, null);

        // When
        boolean result = profile.can("SOURCE_2");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should return false when sourceId does not exist in sources")
    void testCan_withNonExistingSourceId_shouldReturnFalse() {
        // Given
        Set<String> sources = Set.of("SOURCE_1", "SOURCE_2");
        AuthorizationProfile profile = new AuthorizationProfile(null, sources, null);

        // When
        boolean result = profile.can("SOURCE_3");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when sources is null")
    void testCan_withNullSources_shouldReturnFalse() {
        // Given
        AuthorizationProfile profile = new AuthorizationProfile(null, null, null);

        // When
        boolean result = profile.can("SOURCE_1");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return false when sources is empty")
    void testCan_withEmptySources_shouldReturnFalse() {
        // Given
        Set<String> sources = Collections.emptySet();
        AuthorizationProfile profile = new AuthorizationProfile(null, sources, null);

        // When
        boolean result = profile.can("SOURCE_1");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should throw NullPointerException when sourceId is null")
    void testCan_withNullSourceId_shouldThrowNullPointerException() {
        // Given
        Set<String> sources = Set.of("SOURCE_1");
        AuthorizationProfile profile = new AuthorizationProfile(null, sources, null);

        // When/Then
        assertThatThrownBy(() -> profile.can(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should handle case-sensitive source matching")
    void testCan_withCaseSensitiveSource_shouldMatchExactly() {
        // Given
        Set<String> sources = Set.of("Source_1", "SOURCE_2");
        AuthorizationProfile profile = new AuthorizationProfile(null, sources, null);

        // When
        boolean resultLowerCase = profile.can("source_1");
        boolean resultExactMatch = profile.can("Source_1");

        // Then
        assertThat(resultLowerCase).isFalse();
        assertThat(resultExactMatch).isTrue();
    }

    @Test
    @DisplayName("Should handle empty string sourceId")
    void testCan_withEmptyStringSourceId_shouldReturnFalseIfNotInSet() {
        // Given
        Set<String> sources = Set.of("SOURCE_1", "SOURCE_2");
        AuthorizationProfile profile = new AuthorizationProfile(null, sources, null);

        // When
        boolean result = profile.can("");

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should return true when empty string is in sources")
    void testCan_withEmptyStringInSources_shouldReturnTrue() {
        // Given
        Set<String> sources = Set.of("SOURCE_1", "");
        AuthorizationProfile profile = new AuthorizationProfile(null, sources, null);

        // When
        boolean result = profile.can("");

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should handle special characters in sourceId")
    void testCan_withSpecialCharacters_shouldMatchExactly() {
        // Given
        Set<String> sources = Set.of("SOURCE-1", "SOURCE_2", "SOURCE.3", "SOURCE@4");
        AuthorizationProfile profile = new AuthorizationProfile(null, sources, null);

        // When/Then
        assertThat(profile.can("SOURCE-1")).isTrue();
        assertThat(profile.can("SOURCE_2")).isTrue();
        assertThat(profile.can("SOURCE.3")).isTrue();
        assertThat(profile.can("SOURCE@4")).isTrue();
    }

    @Test
    @DisplayName("Should work with mutable HashSet sources")
    void testCan_withMutableSources_shouldWork() {
        // Given
        Set<String> sources = new HashSet<>();
        sources.add("SOURCE_1");
        sources.add("SOURCE_2");
        AuthorizationProfile profile = new AuthorizationProfile(null, sources, null);

        // When
        boolean result = profile.can("SOURCE_1");

        // Then
        assertThat(result).isTrue();
    }
}