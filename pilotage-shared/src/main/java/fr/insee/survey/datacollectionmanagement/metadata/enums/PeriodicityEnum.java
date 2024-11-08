package fr.insee.survey.datacollectionmanagement.metadata.enums;

import lombok.Getter;

@Getter
public enum PeriodicityEnum {

    X("pluriannuel"), A("annuel"), S("semi-annuel"), T("trimestriel"), B("bimensuel"),
    M("mensuel");

    PeriodicityEnum(String value) {
        this.value = value;
    }

    final String value;

}
