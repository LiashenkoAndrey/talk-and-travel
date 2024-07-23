package online.talkandtravel.exception;

import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;

public record ApiExceptionResponse(String message, HttpStatus httpStatus, ZonedDateTime timestamp) {
}
