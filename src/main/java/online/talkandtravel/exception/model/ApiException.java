package online.talkandtravel.exception.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

/**
 * This exception class is parent for other exceptions to an application.
 * It contains a message that will be exposed to a client if it is present
 */
@Getter
@Setter
@ToString
public abstract class ApiException extends RuntimeException {
    private ZonedDateTime zonedDateTime;
    private String messageToClient;
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public ApiException() {
        this.zonedDateTime = ZonedDateTime.now();
    }

    public ApiException(String message) {
        super(message);
        this.zonedDateTime = ZonedDateTime.now();
        this.messageToClient = message;
    }

    public ApiException(String message, HttpStatus httpStatus) {
        super(message);
        this.zonedDateTime = ZonedDateTime.now();
        this.messageToClient = message;
        this.httpStatus = httpStatus;
    }
    public ApiException(String message, String messageToClient, HttpStatus httpStatus) {
        super(message);
        this.zonedDateTime = ZonedDateTime.now();
        this.messageToClient = messageToClient;
        this.httpStatus = httpStatus;
    }
    public ApiException(String message, String messageToClient) {
        super(message);
        this.zonedDateTime = ZonedDateTime.now();
        this.messageToClient = messageToClient;
    }

    public ApiException(String message, boolean hideMessageFromClient) {
        super(message);
        this.zonedDateTime = ZonedDateTime.now();
        if (!hideMessageFromClient) {
            this.messageToClient = message;
        }
    }



    public ApiException(Throwable cause) {
        super(cause);
        this.zonedDateTime = ZonedDateTime.now();
    }

    public ApiExceptionResponse toResponse(HttpStatus status) {
        return new ApiExceptionResponse(this.messageToClient, status, this.zonedDateTime);
    }
}
