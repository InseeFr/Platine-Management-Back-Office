package fr.insee.survey.datacollectionmanagement.questioning.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.insee.survey.datacollectionmanagement.configuration.AuthenticationUserProvider;
import fr.insee.survey.datacollectionmanagement.constants.AuthorityRoleEnum;
import fr.insee.survey.datacollectionmanagement.constants.UrlConstants;
import fr.insee.survey.datacollectionmanagement.questioning.domain.Questioning;
import fr.insee.survey.datacollectionmanagement.questioning.dto.QuestioningProbationDto;
import fr.insee.survey.datacollectionmanagement.questioning.repository.QuestioningRepository;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@ActiveProfiles("test")
class SearchQuestioningControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    QuestioningRepository questioningRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void init() {
        SecurityContextHolder.getContext().setAuthentication(AuthenticationUserProvider.getAuthenticatedUserWithPermissions("test", AuthorityRoleEnum.ADMIN));
    }

    @Test
    void testSearchWithoutParams() throws Exception {
        mockMvc.perform(post(UrlConstants.API_QUESTIONINGS_SEARCH)
                        .content("{}")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "score,ASC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(10)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(20)))
                .andExpect(jsonPath("$.sort.sorted", is(true)));
    }

    @Test
    void testSearchWithParams() throws Exception {
        String json = """
                {
                  "campaignIds": [
                    "SOURCE12023T01"
                  ]
                }
                """;
        mockMvc.perform(post(UrlConstants.API_QUESTIONINGS_SEARCH)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(50)))
                .andExpect(jsonPath("$.sort.sorted", is(false)));
    }

    @Test
    void testPaginationAndSorting() throws Exception {
        mockMvc.perform(post(UrlConstants.API_QUESTIONINGS_SEARCH)
                        .content("{}")
                        .param("page", "1")
                        .param("size", "5")
                        .param("sort", "score,DESC")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number", is(1)))
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.sort.sorted", is(true)));
    }

    @ParameterizedTest
    @CsvSource({"where 1=1,ASC", "status,ASC"})
    void testSortParamsForbiddenThenNoSort(String field, String direction) throws Exception {
        mockMvc.perform(post(UrlConstants.API_QUESTIONINGS_SEARCH)
                        .content("{}")
                        .param("sort", String.format("%s,%s", field, direction))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sort.sorted", is(false)));
    }

    @Test
    void testSearchWithResultBiggerThanPageSize() throws Exception {
        mockMvc.perform(post(UrlConstants.API_QUESTIONINGS_SEARCH)
                        .content("{}")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.size", is(5)))
                .andExpect(jsonPath("$.last", is(false)));
    }

    @Test
    void shouldUpdateProbationStatus() throws Exception {
        // Given
        Questioning existingQuestioning = questioningRepository.findAll().getFirst();
        boolean newStatus = !Boolean.TRUE.equals(existingQuestioning.getIsOnProbation());
        QuestioningProbationDto dto = new QuestioningProbationDto(existingQuestioning.getId(), newStatus);
        String jsonContent = objectMapper.writeValueAsString(dto);

        // When
        mockMvc.perform(put(UrlConstants.API_QUESTIONINGS_PROBATION)
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.questioningId", is(existingQuestioning.getId().toString())))
                .andExpect(jsonPath("$.isOnProbation", is(newStatus)));
    }

    @Test
    void shouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        QuestioningProbationDto dto = new QuestioningProbationDto(nonExistentId, true);
        String jsonContent = objectMapper.writeValueAsString(dto);

        // When
        mockMvc.perform(put(UrlConstants.API_QUESTIONINGS_PROBATION)
                        .content(jsonContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                // Then
                .andExpect(status().isNotFound());
    }

}
