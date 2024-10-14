package online.talkandtravel.exception.model;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

/**
 * Exception class for handling WebSocket-related errors.
 *
 * <p>Includes additional details such as user ID, timestamp, and HTTP status. userId is REQUIRED
 * because every WebSocket exception is sent to * specific user-related path '/user/{userId}/errors
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
    this.zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
    this.messageToClient = message;
  }

  public WebSocketException(String message, HttpStatus httpStatus, Long userId) {
    super(message);
    this.httpStatus = httpStatus;
    this.userId = userId;
    this.messageToClient = message;
    this.zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
  }

  public WebSocketException(HttpException cause, Long userId) {
    super(cause.getMessage());
    this.userId = userId;
    this.httpStatus = cause.getHttpStatus();
    this.messageToClient = cause.getMessage();
    this.zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
  }
}
