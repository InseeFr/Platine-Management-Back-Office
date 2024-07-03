package fr.insee.survey.datacollectionmanagement.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final ApplicationConfig applicationConfig;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this.myLogInterceptor());
        //.addPathPatterns("api/**");
    }

    @Bean
    public LogInterceptor myLogInterceptor() {
        return new LogInterceptor(applicationConfig);
    }

}
