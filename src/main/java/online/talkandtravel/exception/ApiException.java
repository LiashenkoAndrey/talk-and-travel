package online.talkandtravel.exception;

import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;

public record ApiException(String message, HttpStatus httpStatus, ZonedDateTime time) {
}
