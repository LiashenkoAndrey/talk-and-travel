package online.talkandtravel.exception.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/** Global api exception handler. */
@RestControllerAdvice
@Log4j2
public class HttpExceptionHandler {

  @ExceptionHandler(HttpException.class)
  public ResponseEntity<ExceptionResponse> handleApiException(HttpException e) {
    return createResponse(e, e.getHttpStatus());
  }

  @ExceptionHandler({IllegalStateException.class})
  public ExceptionResponse handleIllegalStateException(Exception e) {
    log.error(e.getMessage());
    return new ExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, ZonedDateTime.now());
  }

  /**
   * Handles exceptions of type {@link HandlerMethodValidationException}.
   * This method captures all validation errors and returns a structured
   * response with an appropriate error message and HTTP status.
   *
   * @param e the exception that was thrown when a method validation fails
   * @return an {@link ExceptionResponse} containing a descriptive error message,
   *         the HTTP status code (400 BAD_REQUEST), and the timestamp when the error occurred.
   */
  @ExceptionHandler({HandlerMethodValidationException.class})
  public ExceptionResponse handleValidationExceptions(HandlerMethodValidationException e) {
    String validationResults = e.getAllValidationResults().stream()
            .flatMap((results -> results.getResolvableErrors().stream()))
            .map(this::toValidationResult)
            .collect(Collectors.joining(", "));
    String message = "Validation failure: " + validationResults;

    return new ExceptionResponse(message, HttpStatus.BAD_REQUEST, ZonedDateTime.now());
  }

  //todo: delete or refactor this method
  @ExceptionHandler(value = {ConstraintViolationException.class})
  public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e) {
    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
    for (ConstraintViolation<?> violation : violations) {
      if (violation.getPropertyPath().toString().equals("userName")) {
        ExceptionResponse exceptionResponse =
            new ExceptionResponse(
                violation.getMessage(), HttpStatus.BAD_REQUEST, ZonedDateTime.now());
        return ResponseEntity.badRequest().body(exceptionResponse);
      }
    }
    return ResponseEntity.badRequest()
        .body(
            new ExceptionResponse(
                "Validation failed", HttpStatus.BAD_REQUEST, ZonedDateTime.now()));
  }

  /**
   * Converts a {@link MessageSourceResolvable} into a string representation
   * that combines the field name and the validation error message.
   *
   * @param resolvable a {@link MessageSourceResolvable} containing validation error details
   * @return a string in the format "fieldName: errorMessage"
   *         representing the validation error
   */
  private String toValidationResult(MessageSourceResolvable resolvable) {
    String message = resolvable.getDefaultMessage();
    String field = "unknown field";
    Object[] arguments = resolvable.getArguments();

    if (arguments != null && arguments.length > 0 && arguments[0] instanceof DefaultMessageSourceResolvable arg) {
      field = arg.getDefaultMessage() != null ? arg.getDefaultMessage() : field;
    }

    return field + " " + message;
  }

  /**
   * Creates a user-friendly response
   *
   * @param e exception
   * @param httpStatus http status of response
   * @return user-friendly response
   */
  private ResponseEntity<ExceptionResponse> createResponse(HttpException e, HttpStatus httpStatus) {
    log.error(e.getMessage());
    HttpStatus httpStatus1 = e.getHttpStatus() == null ? httpStatus : e.getHttpStatus();
    return new ResponseEntity<>(e.toResponse(httpStatus1), httpStatus1);
  }
}
