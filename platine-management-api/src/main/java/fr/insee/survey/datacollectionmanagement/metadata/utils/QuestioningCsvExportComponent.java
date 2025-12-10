package fr.insee.survey.datacollectionmanagement.metadata.utils;

import fr.insee.survey.datacollectionmanagement.exception.CsvGenerationException;
import fr.insee.survey.datacollectionmanagement.metadata.dto.QuestioningCsvDto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class QuestioningCsvExportComponent {

    public byte[] toCsvBytes(List<QuestioningCsvDto> data, boolean isBusinessSource) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        CSVFormat format = buildCsvFormat(isBusinessSource);

        try (OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
             CSVPrinter csvPrinter = new CSVPrinter(writer, format)) {

            for (QuestioningCsvDto q : data) {
                List<Object> row = new ArrayList<>();

                row.add(q.partitioningId());
                row.add(q.surveyUnitId());
                row.add(q.interrogationId());
                row.add(q.highestEventType() != null ? q.highestEventType().name() : "");
                row.add(q.highestEventDate() != null ? q.highestEventDate().toString() : "");

                if (isBusinessSource) {
                    row.add(q.isOnProbation());
                }

                csvPrinter.printRecord(row);
            }

            csvPrinter.flush();
        } catch (IOException e) {
            throw new CsvGenerationException("Error generating questioning CSV", e);
        }

        return out.toByteArray();
    }

    private CSVFormat buildCsvFormat(boolean isBusinessSource) {
        List<String> headers = new ArrayList<>();
        headers.add("partitioningId");
        headers.add("surveyUnitId");
        headers.add("interrogationId");
        headers.add("highestEventType");
        headers.add("highestEventDate");

        if (isBusinessSource) {
            headers.add("isOnProbation");
        }

        return CSVFormat.DEFAULT
                .builder()
                .setDelimiter(',')
                .setQuoteMode(QuoteMode.MINIMAL)
                .setHeader(headers.toArray(String[]::new))
                .get();
    }
}
