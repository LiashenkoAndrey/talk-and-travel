package online.talkandtravel.exception.model;

import static online.talkandtravel.exception.util.ExceptionHandlerUtils.VALIDATION_FAILED_MESSAGE;
import static online.talkandtravel.exception.util.ExceptionHandlerUtils.getArgumentValidations;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.file.ImageProcessingException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.ZonedDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

/** Global api exception handler. */
@RestControllerAdvice
@Log4j2
public class HttpExceptionHandler {


  @ExceptionHandler(HttpException.class)
  public ResponseEntity<ExceptionResponse> handleApiException(HttpException e) {
    return createResponse(e, e.getHttpStatus());
  }

  @ExceptionHandler({
      MissingServletRequestPartException.class,
      HttpMediaTypeNotSupportedException.class,
      IllegalStateException.class,
  })
  ResponseEntity<ExceptionResponse> handleMissingServletRequestPartException(
      Exception e, ServletWebRequest request) {
    return createBadRequestResponse(e.getMessage(), request);
  }

  @ExceptionHandler({DataIntegrityViolationException.class, ImageProcessingException.class})
  public ResponseEntity<ExceptionResponse> handleDataIntegrityViolationException(ServletWebRequest request) {
    String message = "Provided data is not valid or can't be processed";
    return createBadRequestResponse(message, request);
  }

  @ExceptionHandler({HttpMessageNotReadableException.class})
  public ResponseEntity<ExceptionResponse> handleMessageNotReadableException(ServletWebRequest request) {
    String message = "The required request body is missing or not readable.";
    return createBadRequestResponse(message, request);
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
  public ResponseEntity<ExceptionResponse> handleMethodValidationExceptions(HandlerMethodValidationException e, ServletWebRequest request) {
    String validationResults = getMethodValidations(e.getAllValidationResults());
    return createBadRequestResponse(VALIDATION_FAILED_MESSAGE + validationResults, request);
  }

  /**
   * Handles exceptions thrown when method argument validation fails.
   *
   * @param e the {@link MethodArgumentNotValidException} that contains validation errors
   * @return an {@link ExceptionResponse} object containing the error message, HTTP status code, and the timestamp
   */
  @ExceptionHandler({MethodArgumentNotValidException.class})
  public ResponseEntity<ExceptionResponse> handleArgumentValidationExceptions(MethodArgumentNotValidException e,  ServletWebRequest request) {
    String validationResults = getArgumentValidations(e.getBindingResult());
    return createBadRequestResponse(VALIDATION_FAILED_MESSAGE + validationResults, request);
  }

  @ExceptionHandler(value = {ConstraintViolationException.class})
  public ResponseEntity<ExceptionResponse> handleConstraintViolationException(ConstraintViolationException e, ServletWebRequest request) {
    Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
    String validationResults = violations.stream()
        .map((violation) -> String.format("'%s' - %s", violation.getPropertyPath(), violation.getMessage()))
        .collect(Collectors.joining(", "));
    return createBadRequestResponse(VALIDATION_FAILED_MESSAGE + validationResults, request);
  }

  private ResponseEntity<ExceptionResponse> createBadRequestResponse(String message, ServletWebRequest request) {
    String requestURI = request.getRequest().getRequestURI();
    log.error("{}, uri: {}", message, requestURI);

    ExceptionResponse response = new ExceptionResponse(
        message,
        HttpStatus.BAD_REQUEST,
        ZonedDateTime.now(),
        requestURI
    );

    return ResponseEntity.badRequest().body(response);
  }


  /**
   * Formats validation errors from a list of {@link ParameterValidationResult}.
   *
   * @param parameterValidations a list of {@link ParameterValidationResult} objects containing parameter validation results
   * @return a formatted string of validation errors, with each error separated by a comma
   */
  private String getMethodValidations(List<ParameterValidationResult> parameterValidations) {
    return parameterValidations.stream()
        .flatMap((results -> results.getResolvableErrors().stream()))
        .map(this::toValidationResult)
        .collect(Collectors.joining(", "));
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
