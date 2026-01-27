package fr.insee.survey.datacollectionmanagement.configuration;

import org.hibernate.cfg.MappingSettings;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateJackson3Configuration {
	@Bean
	HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
		return hibernateProperties ->
                hibernateProperties.put(MappingSettings.JSON_FORMAT_MAPPER,
						Jackson3JsonFormatMapper.class.getName());
	}
}
