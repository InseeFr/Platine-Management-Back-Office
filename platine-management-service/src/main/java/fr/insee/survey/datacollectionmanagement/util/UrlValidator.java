package fr.insee.survey.datacollectionmanagement.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public class UrlValidator {
    private static final String URL_REGEX = "((http|https)://)(www.)?[a-zA-Z0-9@:%._\\+~#?&//=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%._\\+~#?&//=]*)"; //NOSONAR

    public static boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }
}
