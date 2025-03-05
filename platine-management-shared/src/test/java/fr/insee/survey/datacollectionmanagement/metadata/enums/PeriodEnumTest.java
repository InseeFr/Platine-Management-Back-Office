package fr.insee.survey.datacollectionmanagement.metadata.enums;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PeriodEnumTest {


    @ParameterizedTest
    @MethodSource("providePeriodEnums")
    void testPeriodEnums(PeriodEnum period, PeriodicityEnum expectedPeriodicity, String expectedValue) {
        assertEquals(expectedPeriodicity, period.getPeriod());
        assertEquals(expectedValue, period.getValue());
    }

    private static Stream<Arguments> providePeriodEnums() {
        return Stream.of(
                Arguments.of(PeriodEnum.A00, PeriodicityEnum.A, PeriodNameEnum.ANNUAL.getValue()),
                Arguments.of(PeriodEnum.X00, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X00"),
                Arguments.of(PeriodEnum.X01, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X01"),
                Arguments.of(PeriodEnum.X02, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X02"),
                Arguments.of(PeriodEnum.X03, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X03"),
                Arguments.of(PeriodEnum.X04, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X04"),
                Arguments.of(PeriodEnum.X05, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X05"),
                Arguments.of(PeriodEnum.X06, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X06"),
                Arguments.of(PeriodEnum.X07, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X07"),
                Arguments.of(PeriodEnum.X08, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X08"),
                Arguments.of(PeriodEnum.X09, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X09"),
                Arguments.of(PeriodEnum.X10, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X10"),
                Arguments.of(PeriodEnum.X11, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X11"),
                Arguments.of(PeriodEnum.X12, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X12"),
                Arguments.of(PeriodEnum.X13, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X13"),
                Arguments.of(PeriodEnum.X14, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X14"),
                Arguments.of(PeriodEnum.X15, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X15"),
                Arguments.of(PeriodEnum.X16, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X16"),
                Arguments.of(PeriodEnum.X17, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X17"),
                Arguments.of(PeriodEnum.X18, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X18"),
                Arguments.of(PeriodEnum.X19, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X19"),
                Arguments.of(PeriodEnum.X20, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X20"),
                Arguments.of(PeriodEnum.X21, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X21"),
                Arguments.of(PeriodEnum.X22, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X22"),
                Arguments.of(PeriodEnum.X23, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X23"),
                Arguments.of(PeriodEnum.X24, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X24"),
                Arguments.of(PeriodEnum.X25, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X25"),
                Arguments.of(PeriodEnum.X26, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X26"),
                Arguments.of(PeriodEnum.X27, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X27"),
                Arguments.of(PeriodEnum.X28, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X28"),
                Arguments.of(PeriodEnum.X29, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X29"),
                Arguments.of(PeriodEnum.X30, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X30"),
                Arguments.of(PeriodEnum.X31, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X31"),
                Arguments.of(PeriodEnum.X32, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X32"),
                Arguments.of(PeriodEnum.X33, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X33"),
                Arguments.of(PeriodEnum.X34, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X34"),
                Arguments.of(PeriodEnum.X35, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X35"),
                Arguments.of(PeriodEnum.X36, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X36"),
                Arguments.of(PeriodEnum.X37, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X37"),
                Arguments.of(PeriodEnum.X38, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X38"),
                Arguments.of(PeriodEnum.X39, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X39"),
                Arguments.of(PeriodEnum.X40, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X40"),
                Arguments.of(PeriodEnum.X41, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X41"),
                Arguments.of(PeriodEnum.X42, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X42"),
                Arguments.of(PeriodEnum.X43, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X43"),
                Arguments.of(PeriodEnum.X44, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X44"),
                Arguments.of(PeriodEnum.X45, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X45"),
                Arguments.of(PeriodEnum.X46, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X46"),
                Arguments.of(PeriodEnum.X47, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X47"),
                Arguments.of(PeriodEnum.X48, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X48"),
                Arguments.of(PeriodEnum.X49, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X49"),
                Arguments.of(PeriodEnum.X50, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X50"),
                Arguments.of(PeriodEnum.X51, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X51"),
                Arguments.of(PeriodEnum.X52, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X52"),
                Arguments.of(PeriodEnum.X53, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X53"),
                Arguments.of(PeriodEnum.X54, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X54"),
                Arguments.of(PeriodEnum.X55, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X55"),
                Arguments.of(PeriodEnum.X56, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X56"),
                Arguments.of(PeriodEnum.X57, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X57"),
                Arguments.of(PeriodEnum.X58, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X58"),
                Arguments.of(PeriodEnum.X59, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X59"),
                Arguments.of(PeriodEnum.X60, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X60"),
                Arguments.of(PeriodEnum.X61, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X61"),
                Arguments.of(PeriodEnum.X62, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X62"),
                Arguments.of(PeriodEnum.X63, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X63"),
                Arguments.of(PeriodEnum.X64, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X64"),
                Arguments.of(PeriodEnum.X65, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X65"),
                Arguments.of(PeriodEnum.X66, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X66"),
                Arguments.of(PeriodEnum.X67, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X67"),
                Arguments.of(PeriodEnum.X68, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X68"),
                Arguments.of(PeriodEnum.X69, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X69"),
                Arguments.of(PeriodEnum.X70, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X70"),
                Arguments.of(PeriodEnum.X71, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X71"),
                Arguments.of(PeriodEnum.X72, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X72"),
                Arguments.of(PeriodEnum.X73, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X73"),
                Arguments.of(PeriodEnum.X74, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X74"),
                Arguments.of(PeriodEnum.X75, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X75"),
                Arguments.of(PeriodEnum.X76, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X76"),
                Arguments.of(PeriodEnum.X77, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X77"),
                Arguments.of(PeriodEnum.X78, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X78"),
                Arguments.of(PeriodEnum.X79, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X79"),
                Arguments.of(PeriodEnum.X80, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X80"),
                Arguments.of(PeriodEnum.X81, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X81"),
                Arguments.of(PeriodEnum.X82, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X82"),
                Arguments.of(PeriodEnum.X83, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X83"),
                Arguments.of(PeriodEnum.X84, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X84"),
                Arguments.of(PeriodEnum.X85, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X85"),
                Arguments.of(PeriodEnum.X86, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X86"),
                Arguments.of(PeriodEnum.X87, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X87"),
                Arguments.of(PeriodEnum.X88, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X88"),
                Arguments.of(PeriodEnum.X89, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X89"),
                Arguments.of(PeriodEnum.X90, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X90"),
                Arguments.of(PeriodEnum.X91, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X91"),
                Arguments.of(PeriodEnum.X92, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X92"),
                Arguments.of(PeriodEnum.X93, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X93"),
                Arguments.of(PeriodEnum.X94, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X94"),
                Arguments.of(PeriodEnum.X95, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X95"),
                Arguments.of(PeriodEnum.X96, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X96"),
                Arguments.of(PeriodEnum.X97, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X97"),
                Arguments.of(PeriodEnum.X98, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X98"),
                Arguments.of(PeriodEnum.X99, PeriodicityEnum.X, PeriodNameEnum.PLURIANNUAL.getValue() + " X99"),
                Arguments.of(PeriodEnum.S01, PeriodicityEnum.S, PeriodNameEnum.FIRST_SEMESTER.getValue()),
                Arguments.of(PeriodEnum.S02, PeriodicityEnum.S, PeriodNameEnum.SECOND_SEMESTER.getValue()),
                Arguments.of(PeriodEnum.T01, PeriodicityEnum.T, PeriodNameEnum.FIRST_TRIMESTER.getValue()),
                Arguments.of(PeriodEnum.T02, PeriodicityEnum.T, PeriodNameEnum.SECOND_TRIMESTER.getValue()),
                Arguments.of(PeriodEnum.T03, PeriodicityEnum.T, PeriodNameEnum.THIRD_TRIMESTER.getValue()),
                Arguments.of(PeriodEnum.T04, PeriodicityEnum.T, PeriodNameEnum.FOURTH_TRIMESTER.getValue()),
                Arguments.of(PeriodEnum.M01, PeriodicityEnum.M, PeriodNameEnum.JANUARY.getValue()),
                Arguments.of(PeriodEnum.M02, PeriodicityEnum.M, PeriodNameEnum.FEBRUARY.getValue()),
                Arguments.of(PeriodEnum.M03, PeriodicityEnum.M, PeriodNameEnum.MARCH.getValue()),
                Arguments.of(PeriodEnum.M04, PeriodicityEnum.M, PeriodNameEnum.APRIL.getValue()),
                Arguments.of(PeriodEnum.M05, PeriodicityEnum.M, PeriodNameEnum.MAY.getValue()),
                Arguments.of(PeriodEnum.M06, PeriodicityEnum.M, PeriodNameEnum.JUNE.getValue()),
                Arguments.of(PeriodEnum.M07, PeriodicityEnum.M, PeriodNameEnum.JULY.getValue()),
                Arguments.of(PeriodEnum.M08, PeriodicityEnum.M, PeriodNameEnum.AUGUST.getValue()),
                Arguments.of(PeriodEnum.M09, PeriodicityEnum.M, PeriodNameEnum.SEPTEMBER.getValue()),
                Arguments.of(PeriodEnum.M10, PeriodicityEnum.M, PeriodNameEnum.OCTOBER.getValue()),
                Arguments.of(PeriodEnum.M11, PeriodicityEnum.M, PeriodNameEnum.NOVEMBER.getValue()),
                Arguments.of(PeriodEnum.M12, PeriodicityEnum.M, PeriodNameEnum.DECEMBER.getValue()),
                Arguments.of(PeriodEnum.B01, PeriodicityEnum.B, PeriodNameEnum.FIRST_BIMESTER.getValue()),
                Arguments.of(PeriodEnum.B02, PeriodicityEnum.B, PeriodNameEnum.SECOND_BIMESTER.getValue()),
                Arguments.of(PeriodEnum.B03, PeriodicityEnum.B, PeriodNameEnum.THIRD_BIMESTER.getValue()),
                Arguments.of(PeriodEnum.B04, PeriodicityEnum.B, PeriodNameEnum.FOURTH_BIMESTER.getValue()),
                Arguments.of(PeriodEnum.B05, PeriodicityEnum.B, PeriodNameEnum.FIFTH_BIMESTER.getValue()),
                Arguments.of(PeriodEnum.B06, PeriodicityEnum.B, PeriodNameEnum.SIXTH_BIMESTER.getValue())
        );

    }
}