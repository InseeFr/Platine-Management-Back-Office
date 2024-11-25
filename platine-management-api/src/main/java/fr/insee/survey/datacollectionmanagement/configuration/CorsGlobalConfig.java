package fr.insee.survey.datacollectionmanagement.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class CorsGlobalConfig {

    private final ApplicationConfig applicationConfig;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                String[] ao =  applicationConfig.getAllowedOrigins();
                registry.addMapping("/**")
                        .allowedOrigins(ao)
                        .allowedMethods("POST", "GET", "PUT", "OPTIONS", "DELETE")
                        .allowedHeaders("Authorization", "Origin", "X-Requested-With", "Content-Type", "Accept")
                        .maxAge(3600);
            }
        };
    }
}
