package online.talkandtravel.exception.model;

import jakarta.servlet.http.HttpServletRequest;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * This exception class is parent for other exceptions to an application.
 * It contains a message that will be exposed to a client if it is present
 */
@Getter
@Setter
@ToString
public abstract class HttpException extends RuntimeException {
    private ZonedDateTime zonedDateTime;
    private String messageToClient;
    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public HttpException() {
        this.zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
    }

    public HttpException(String message) {
        super(message);
        this.zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        this.messageToClient = message;
    }

    public HttpException(String message, HttpStatus httpStatus) {
        super(message);
        this.zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        this.messageToClient = message;
        this.httpStatus = httpStatus;
    }
    public HttpException(String message, String messageToClient, HttpStatus httpStatus) {
        super(message);
        this.zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        this.messageToClient = messageToClient;
        this.httpStatus = httpStatus;
    }
    public HttpException(String message, String messageToClient) {
        super(message);
        this.zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        this.messageToClient = messageToClient;
    }

    public HttpException(String message, boolean hideMessageFromClient) {
        super(message);
        this.zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
        if (!hideMessageFromClient) {
            this.messageToClient = message;
        }
    }

    public HttpException(Throwable cause) {
        super(cause);
        this.zonedDateTime = ZonedDateTime.now(ZoneOffset.UTC);
    }

    public ExceptionResponse toResponse(HttpStatus status) {
        return new ExceptionResponse(this.messageToClient, status, this.zonedDateTime, getRequestUri());
    }

    /**
     * Retrieves the URI of the current HTTP request.
     * <p>
     * This method uses {@link RequestContextHolder} to access the {@link ServletRequestAttributes}
     * of the current request, allowing it to be used in service, exception, or utility classes
     * without needing to pass {@link HttpServletRequest} directly.
     * If no request is present (e.g., in a non-HTTP context), the method returns {@code null}.
     * </p>
     *
     * @return the request URI as a {@link String}, or {@code null} if no request is available
     *         in the current context.
     */
    public String getRequestUri() {
        Optional<ServletRequestAttributes> request = Optional.ofNullable(
            ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()));
        return request.map((e) -> e.getRequest().getRequestURI())
            .orElse(null);
    }
}
