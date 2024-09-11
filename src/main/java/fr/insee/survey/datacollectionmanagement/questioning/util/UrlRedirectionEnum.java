package fr.insee.survey.datacollectionmanagement.questioning.util;

public enum UrlRedirectionEnum {
    POOL1("pool1"),
    POOL2("pool2");

    final String pool;
    UrlRedirectionEnum(String pool) {
        this.pool=pool;
    }
}
