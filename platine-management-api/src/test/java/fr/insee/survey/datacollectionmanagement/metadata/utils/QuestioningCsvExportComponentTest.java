package fr.insee.survey.datacollectionmanagement.metadata.utils;

import fr.insee.survey.datacollectionmanagement.metadata.dto.QuestioningCsvDto;
import fr.insee.survey.datacollectionmanagement.questioning.enums.TypeQuestioningEvent;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class QuestioningCsvExportComponentTest {

    private final QuestioningCsvExportComponent component = new QuestioningCsvExportComponent();

    @Test
    void toCsvBytes_businessSource_shouldIncludeIsOnProbationColumn() throws Exception {
        // given
        QuestioningCsvDto dto = new QuestioningCsvDto(
                UUID.fromString("00000000-0000-0000-0000-000000000001"),
                "PART1",
                "SU1",
                TypeQuestioningEvent.INITLA,
                new Date(0L),
                true
        );

        // when
        CSVParser parser = generateAndParseCsv(List.of(dto), true);

        // then
        List<String> headers = parser.getHeaderNames();
        assertThat(headers)
                .containsExactly(
                        "partitioningId",
                        "surveyUnitId",
                        "interrogationId",
                        "highestEventType",
                        "highestEventDate",
                        "isOnProbation"
                );

        List<CSVRecord> records = parser.getRecords();
        assertThat(records).hasSize(1);

        CSVRecord csvRecord = records.getFirst();
        assertThat(csvRecord.get("partitioningId")).isEqualTo("PART1");
        assertThat(csvRecord.get("surveyUnitId")).isEqualTo("SU1");
        assertThat(csvRecord.get("interrogationId"))
                .isEqualTo("00000000-0000-0000-0000-000000000001");
        assertThat(csvRecord.get("highestEventType")).isEqualTo("INITLA");
        assertThat(csvRecord.get("highestEventDate")).isNotBlank();
        assertThat(csvRecord.get("isOnProbation")).isEqualTo("true");
    }

    @Test
    void toCsvBytes_nonBusinessSource_shouldNotIncludeIsOnProbationColumn() throws Exception {
        // given
        QuestioningCsvDto dto = new QuestioningCsvDto(
                UUID.fromString("00000000-0000-0000-0000-000000000002"),
                "PART2",
                "SU2",
                TypeQuestioningEvent.EXPERT,
                new Date(0L),
                false
        );

        // when
        CSVParser parser = generateAndParseCsv(List.of(dto), false);

        // then
        List<String> headers = parser.getHeaderNames();
        assertThat(headers)
                .containsExactly(
                        "partitioningId",
                        "surveyUnitId",
                        "interrogationId",
                        "highestEventType",
                        "highestEventDate"
                )
                .doesNotContain("isOnProbation");

        List<CSVRecord> records = parser.getRecords();
        assertThat(records).hasSize(1);

        CSVRecord csvRecord = records.getFirst();
        assertThat(csvRecord.get("partitioningId")).isEqualTo("PART2");
        assertThat(csvRecord.get("surveyUnitId")).isEqualTo("SU2");
        assertThat(csvRecord.get("interrogationId"))
                .isEqualTo("00000000-0000-0000-0000-000000000002");
        assertThat(csvRecord.get("highestEventType")).isEqualTo("EXPERT");
        assertThat(csvRecord.get("highestEventDate")).isNotBlank();
    }

    @Test
    void toCsvBytes_shouldHandleNullEventTypeAndDate() throws Exception {
        // given
        QuestioningCsvDto dto = new QuestioningCsvDto(
                UUID.fromString("00000000-0000-0000-0000-000000000003"),
                "PART3",
                "SU3",
                null,       // highestEventType null
                null,       // highestEventDate null
                true
        );

        // when
        CSVParser parser = generateAndParseCsv(List.of(dto), true);

        // then
        List<String> headers = parser.getHeaderNames();
        assertThat(headers)
                .containsExactly(
                        "partitioningId",
                        "surveyUnitId",
                        "interrogationId",
                        "highestEventType",
                        "highestEventDate",
                        "isOnProbation"
                );

        List<CSVRecord> records = parser.getRecords();
        assertThat(records).hasSize(1);

        CSVRecord csvRecord = records.getFirst();
        assertThat(csvRecord.get("partitioningId")).isEqualTo("PART3");
        assertThat(csvRecord.get("surveyUnitId")).isEqualTo("SU3");
        assertThat(csvRecord.get("interrogationId"))
                .isEqualTo("00000000-0000-0000-0000-000000000003");
        assertThat(csvRecord.get("highestEventType")).isEmpty();
        assertThat(csvRecord.get("highestEventDate")).isEmpty();
        assertThat(csvRecord.get("isOnProbation")).isEqualTo("true");
    }

    @Test
    void toCsvBytes_shouldHandleEmptyDataList() throws Exception {
        // given
        List<QuestioningCsvDto> data = List.of(); // liste vide

        // when
        CSVParser parser = generateAndParseCsv(data, false);

        // then
        List<String> headers = parser.getHeaderNames();
        assertThat(headers)
                .containsExactly(
                        "partitioningId",
                        "surveyUnitId",
                        "interrogationId",
                        "highestEventType",
                        "highestEventDate"
                );

        List<CSVRecord> records = parser.getRecords();
        assertThat(records).isEmpty();
    }

    @Test
    void toCsvBytes_shouldHandleThousandDtos_forBusinessSource() throws Exception {
        // given
        int size = 1000;
        List<QuestioningCsvDto> data = IntStream.range(0, size)
                .mapToObj(i -> new QuestioningCsvDto(
                        UUID.randomUUID(),
                        "PART" + i,
                        "SU" + i,
                        TypeQuestioningEvent.INITLA,
                        new Date(i),
                        i % 2 == 0
                ))
                .toList();

        // when
        CSVParser parser = generateAndParseCsv(data, true);

        // then
        List<String> headers = parser.getHeaderNames();
        assertThat(headers)
                .containsExactly(
                        "partitioningId",
                        "surveyUnitId",
                        "interrogationId",
                        "highestEventType",
                        "highestEventDate",
                        "isOnProbation"
                );


        List<CSVRecord> records = parser.getRecords();
        assertThat(records).hasSize(size);

        CSVRecord first = records.getFirst();
        assertThat(first.get("partitioningId")).isEqualTo("PART0");
        assertThat(first.get("surveyUnitId")).isEqualTo("SU0");
        assertThat(first.get("highestEventType")).isEqualTo("INITLA");
        assertThat(first.get("isOnProbation")).isEqualTo("true");

        CSVRecord last = records.get(size - 1);
        assertThat(last.get("partitioningId")).isEqualTo("PART" + (size - 1));
        assertThat(last.get("surveyUnitId")).isEqualTo("SU" + (size - 1));
        assertThat(last.get("highestEventType")).isEqualTo("INITLA");
        assertThat(last.get("isOnProbation")).isEqualTo("false");
    }

    private CSVParser generateAndParseCsv(List<QuestioningCsvDto> data, boolean isBusinessSource) throws IOException {
        byte[] bytes = component.toCsvBytes(data, isBusinessSource);
        String csv = new String(bytes, StandardCharsets.UTF_8);

        CSVFormat format = CSVFormat.DEFAULT
                .builder()
                .setDelimiter(',')
                .setHeader()
                .setSkipHeaderRecord(false)
                .get();

        return CSVParser.parse(csv, format);
    }

}