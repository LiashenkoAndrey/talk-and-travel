package online.talkandtravel.exception.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** Global api exception handler. */
@RestControllerAdvice
@Log4j2
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler({ApiException.class})
  public ResponseEntity<ApiExceptionResponse> handleApiException(ApiException e, ServletWebRequest request) {
    return createResponse(e, e.getHttpStatus());
  }

  @ExceptionHandler({IllegalStateException.class})
  public ResponseEntity<ApiExceptionResponse> handleInternalExceptions(Exception e) {
    log.error(e.getMessage());
    ApiExceptionResponse apiExceptionResponse =
        new ApiExceptionResponse("internal", HttpStatus.INTERNAL_SERVER_ERROR, ZonedDateTime.now());
    return new ResponseEntity<>(apiExceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = {ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
    for (ConstraintViolation<?> violation : violations) {
      if (violation.getPropertyPath().toString().equals("userName")) {
        ApiExceptionResponse apiExceptionResponse =
            new ApiExceptionResponse(
                violation.getMessage(), HttpStatus.BAD_REQUEST, ZonedDateTime.now());
        return ResponseEntity.badRequest().body(apiExceptionResponse);
      }
    }
    return ResponseEntity.badRequest()
        .body(
            new ApiExceptionResponse(
                "Validation failed", HttpStatus.BAD_REQUEST, ZonedDateTime.now()));
  }

  /**
   * Creates a user-friendly response
   *
   * @param e exception
   * @param httpStatus http status of response
   * @return user-friendly response
   */
  private ResponseEntity<ApiExceptionResponse> createResponse(
      ApiException e, HttpStatus httpStatus) {
    log.error(e.getMessage());
    HttpStatus httpStatus1 = e.getHttpStatus() == null ? httpStatus : e.getHttpStatus();
    return new ResponseEntity<>(e.toResponse(httpStatus1), httpStatus1);
  }
}
