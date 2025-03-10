package fr.insee.survey.datacollectionmanagement.configuration;

import fr.insee.survey.datacollectionmanagement.configuration.auth.user.AuthenticationUserHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthenticationUserHelper authenticationUserHelper;

    private final ApplicationConfig applicationConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.myLogInterceptor());
    }

    @Bean
    public LogInterceptor myLogInterceptor() {
        return new LogInterceptor(authenticationUserHelper);
    }

    @Bean
    public String lunaticNormalUrl() {
        return applicationConfig.getLunaticNormalUrl();
    }

    @Bean
    public String lunaticSensitiveUrl() {
        return applicationConfig.getLunaticSensitiveUrl();
    }

    @Bean
    public String xform1Url() {
        return applicationConfig.getXform1Url();
    }

    @Bean
    public String xform2Url() {
        return applicationConfig.getXform2Url();
    }

    @Bean
    public String questionnaireApiUrl() {
        return applicationConfig.getQuestionnaireApiUrl();
    }

    @Bean
    public String questionnaireApiSensitiveUrl() {
        return applicationConfig.getQuestionnaireApiSensitiveUrl();
    }

}
