package fr.insee.survey.datacollectionmanagement.configuration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@TestConfiguration
public class FixedTimeConfiguration {

    @Bean(name = "testClock")
    @Primary
    public Clock clock() {
        Instant fixedInstant = Instant.ofEpochMilli(1747395350727L);
        return Clock.fixed(fixedInstant, ZoneOffset.UTC);
    }
}
