package online.talkandtravel.exception.model;

import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 * Exception class for handling WebSocket-related errors.
 *
 * <p>Includes additional details such as user ID, timestamp, and HTTP status.
 */
@ToString
@Getter
@Setter
public class WebSocketException extends RuntimeException {

  private Long userId;
  private ZonedDateTime zonedDateTime;
  private String messageToClient;
  private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

  public WebSocketException(String message, Long userId) {
    super(message);
    this.userId = userId;
    this.zonedDateTime = ZonedDateTime.now();
    this.messageToClient = message;
  }

  public WebSocketException(String message, HttpStatus httpStatus, Long userId) {
    super(message);
    this.httpStatus = httpStatus;
    this.userId = userId;
    this.messageToClient = message;
    this.zonedDateTime = ZonedDateTime.now();
  }

  public WebSocketException(HttpException cause, Long userId) {
    super(cause.getMessage());
    this.userId = userId;
    this.httpStatus = cause.getHttpStatus();
    this.messageToClient = cause.getMessage();
    this.zonedDateTime = ZonedDateTime.now();
  }

  public WebSocketException(HttpException cause) {
    super(cause.getMessage());
    this.httpStatus = cause.getHttpStatus();
    this.messageToClient = cause.getMessage();
    this.zonedDateTime = ZonedDateTime.now();
  }
}
