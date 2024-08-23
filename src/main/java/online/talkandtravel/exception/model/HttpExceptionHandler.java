package online.talkandtravel.exception.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.*;

import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.validation.ValidationResult;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

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
  @ExceptionHandler({HandlerMethodValidationException.class})
  public ExceptionResponse handleValidationExceptions(HandlerMethodValidationException e) {

    List<ParameterValidationResult> results = e.getAllValidationResults();
    for (ParameterValidationResult result : results) {
      List<MessageSourceResolvable> resolvables = result.getResolvableErrors();
      List<ValidationResult> validationResults = resolvables.stream()
              .map(this::toValidationResult)
              .toList();
      log.info(validationResults);
      return new ExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, ZonedDateTime.now(), validationResults);
    }
    return new ExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST, ZonedDateTime.now());
  }



  private ValidationResult toValidationResult(MessageSourceResolvable resolvable) {
    DefaultMessageSourceResolvable arg = (DefaultMessageSourceResolvable) Objects.requireNonNull(resolvable.getArguments())[0];
    String field = arg.getDefaultMessage();

    String message = resolvable.getDefaultMessage();

    return new ValidationResult(field,  message);
  }

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
