package online.talkandtravel.exception.model;

import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;

public record ExceptionResponse(
    String message,
    HttpStatus httpStatus,
    ZonedDateTime timestamp,
    String uri) {
}
