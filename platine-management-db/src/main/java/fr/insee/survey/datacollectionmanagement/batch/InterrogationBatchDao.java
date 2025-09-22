package fr.insee.survey.datacollectionmanagement.batch;

import fr.insee.survey.datacollectionmanagement.batch.model.Interrogation;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
@RequiredArgsConstructor
public class InterrogationBatchDao implements InterrogationBatchRepository {
    private final JdbcTemplate jdbc;

    @Override
    @Transactional
    public void upsert(Interrogation interrogation) {
        /*final String upsertInterrogation = """
            INSERT INTO interrogation (id, survey_unit_id, campaign_id, questionnaire_model_id)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (id) DO UPDATE SET
                survey_unit_id = EXCLUDED.survey_unit_id,
                campaign_id = EXCLUDED.campaign_id,
                questionnaire_model_id = EXCLUDED.questionnaire_model_id
        """;

        jdbc.batchUpdate(upsertInterrogation, interrogations, interrogations.size(), (preparedStatement, interrogation) -> {
            preparedStatement.setString(1, interrogation.id());
            preparedStatement.setString(2, interrogation.surveyUnitId());
            preparedStatement.setString(3, interrogation.campaignId());
            preparedStatement.setString(4, interrogation.questionnaireId());
        });*/
    }
}
