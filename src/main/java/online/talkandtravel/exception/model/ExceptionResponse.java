package online.talkandtravel.exception.model;

import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;

public record ExceptionResponse(String message, HttpStatus httpStatus, ZonedDateTime timestamp, String uri) {

  public ExceptionResponse(String message, HttpStatus httpStatus, ZonedDateTime timestamp,
      String uri) {
    this.message = message;
    this.httpStatus = httpStatus;
    this.timestamp = timestamp;
    this.uri = uri;
  }

  public ExceptionResponse(String message, HttpStatus httpStatus, ZonedDateTime timestamp) {
    this(message, httpStatus, timestamp, null);
  }

}
