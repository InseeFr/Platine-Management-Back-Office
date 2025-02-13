package fr.insee.survey.datacollectionmanagement.metadata.enums;

import lombok.Getter;

@Getter
public enum DataCollectionEnum {

    LUNATIC_SENSITIVE("lunatic_sensitive"), LUNATIC_NORMAL("lunatic_normal"), XFORM1("xform1"), XFORM2("xform2");
    final String value;

    DataCollectionEnum(String v) {
        value = v;
    }
}
