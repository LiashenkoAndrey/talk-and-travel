package online.talkandtravel.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.Set;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global api exception handler.
 */
@ControllerAdvice
@Log4j2
public class ApiExceptionHandler {
    @ExceptionHandler({AuthenticationException.class, RegistrationException.class,
            NoSuchElementException.class,
            UnsupportedFormatException.class, FileSizeExceededException.class,
            ImageWriteException.class, RuntimeException.class, ImageProcessingException.class})
    public ResponseEntity<ApiExceptionResponse> handleException(ApiException e) {
        return createResponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UserNotFoundException.class, UsernameNotFoundException.class})
    public ResponseEntity<ApiExceptionResponse> handleNotFoundException(ApiException e) {
        return createResponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            if (violation.getPropertyPath().toString().equals("userName")) {
                ApiExceptionResponse apiExceptionResponse = new ApiExceptionResponse(
                        violation.getMessage(),
                        HttpStatus.BAD_REQUEST,
                        ZonedDateTime.now()
                );
                return ResponseEntity.badRequest().body(apiExceptionResponse);
            }
        }
        return ResponseEntity.badRequest().body(new ApiExceptionResponse("Validation failed",
                HttpStatus.BAD_REQUEST, ZonedDateTime.now()));
    }

    /**
     * Creates a user-friendly response
     * @param e exception
     * @param httpStatus http status of response
     * @return user-friendly response
     */
    private ResponseEntity<ApiExceptionResponse> createResponse(ApiException e, HttpStatus httpStatus) {
        log.error(e.getMessage(), e);
        HttpStatus httpStatus1 = e.getHttpStatus() == null ? httpStatus : e.getHttpStatus();
        return new ResponseEntity<>(e.toResponse(httpStatus1), httpStatus1);
    }
}