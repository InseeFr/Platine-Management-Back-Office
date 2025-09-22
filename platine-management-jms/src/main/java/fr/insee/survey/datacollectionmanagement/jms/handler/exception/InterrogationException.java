package fr.insee.survey.datacollectionmanagement.jms.handler.exception;

public class InterrogationException extends RuntimeException {
    public static final String ALREADY_EXECUTED_MESSAGE = "Command %s with same execution already executed. Command %s aborted";


    public InterrogationException(String executedCommandId, String abortedCommandId) {
        super(String.format(ALREADY_EXECUTED_MESSAGE, executedCommandId, abortedCommandId));
    }
}
