package online.talkandtravel.exception.util;

import online.talkandtravel.exception.model.HttpException;
import org.springframework.http.HttpStatus;

public class StringParseException extends HttpException {
    private static final String MESSAGE = "Provided string %s can't be parsed";
    private static final HttpStatus STATUS = HttpStatus.CONFLICT;

    public StringParseException(String string, String description) {
        super(String.format(MESSAGE, string) + " " + description, STATUS);
    }
}
