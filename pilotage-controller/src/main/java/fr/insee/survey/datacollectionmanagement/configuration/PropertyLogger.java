package fr.insee.survey.datacollectionmanagement.configuration;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.StreamSupport;

@Component
@Slf4j
public class PropertyLogger  {

    private static boolean alreadyDisplayed=false;

    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        final Environment env = event.getApplicationContext().getEnvironment();

        if (!alreadyDisplayed) {


            log.info("================================ Properties ================================");
            final MutablePropertySources sources = ((AbstractEnvironment) env).getPropertySources();
            StreamSupport.stream(sources.spliterator(), false)
                    .filter(ps -> ps instanceof EnumerablePropertySource)
                    .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                    .flatMap(Arrays::stream)
                    .distinct()
                    .filter(prop -> !(prop.contains("credentials") || prop.contains("password")
                            || prop.contains("pw") || prop.contains("Password")))
                    .filter(prop -> prop.startsWith("fr.insee") || prop.startsWith("logging") || prop.startsWith("jwt") || prop.startsWith("spring"))
                    .sorted()
                    .forEach(prop -> log.info("{}: {}", prop, env.getProperty(prop)));
            log.info("===========================================================================");
        }
        alreadyDisplayed=true;
    }
}