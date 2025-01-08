package fr.insee.survey.datacollectionmanagement.metadata.enums;

public enum PeriodNameEnum {
    ANNUAL("annuel"),
    PLURIANNUAL("pluriannuel"),
    FIRST_SEMESTER("1er semestre"),
    SECOND_SEMESTER("2nd semestre"),
    FIRST_TRIMESTER("1er trimestre"),
    SECOND_TRIMESTER("2e trimestre"),
    THIRD_TRIMESTER("3e trimestre"),
    FOURTH_TRIMESTER("4e trimestre"),
    JANUARY("janvier"),
    FEBRUARY("février"),
    MARCH("mars"),
    APRIL("avril"),
    MAY("mai"),
    JUNE("juin"),
    JULY("juillet"),
    AUGUST("août"),
    SEPTEMBER("septembre"),
    OCTOBER("octobre"),
    NOVEMBER("novembre"),
    DECEMBER("décembre"),
    FIRST_BIMESTER("1er bimestre"),
    SECOND_BIMESTER("2e bimestre"),
    THIRD_BIMESTER("3e bimestre"),
    FOURTH_BIMESTER("4e bimestre"),
    FIFTH_BIMESTER("5e bimestre"),
    SIXTH_BIMESTER("6e bimestre");

    private final String value;

    PeriodNameEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
