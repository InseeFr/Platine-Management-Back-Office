package fr.insee.survey.datacollectionmanagement.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public class UrlValidator {

    public static boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
}
