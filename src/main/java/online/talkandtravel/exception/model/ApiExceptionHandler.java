package online.talkandtravel.exception.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.Set;

import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.auth.AuthenticationException;
import online.talkandtravel.exception.chat.MainCountryChatNotFoundException;
import online.talkandtravel.exception.country.CountryNotFoundException;
import online.talkandtravel.exception.data.FailedToReadJsonException;
import online.talkandtravel.exception.file.FileSizeExceededException;
import online.talkandtravel.exception.file.ImageProcessingException;
import online.talkandtravel.exception.file.ImageWriteException;
import online.talkandtravel.exception.auth.RegistrationException;
import online.talkandtravel.exception.file.UnsupportedFormatException;
import online.talkandtravel.exception.user.UserAlreadyJoinTheChatException;
import online.talkandtravel.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/** Global api exception handler. */
@ControllerAdvice
@Log4j2
public class ApiExceptionHandler {
  @ExceptionHandler({
      ApiException.class
  })
  public ResponseEntity<ApiExceptionResponse> handleApiException(ApiException e) {
    return createResponse(e, e.getHttpStatus());
  }

  @ExceptionHandler({
    AuthenticationException.class,
    RegistrationException.class,
    NoSuchElementException.class,
    UnsupportedFormatException.class,
    FileSizeExceededException.class,
    ImageWriteException.class,
    ImageProcessingException.class,
    UserAlreadyJoinTheChatException.class,
      FailedToReadJsonException.class,
      UserAlreadyJoinTheChatException.class
  })
  public ResponseEntity<ApiExceptionResponse> handleException(ApiException e) {
    return createResponse(e, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({
    UserNotFoundException.class,
    UsernameNotFoundException.class,
    MainCountryChatNotFoundException.class,
      CountryNotFoundException.class
  })
  public ResponseEntity<ApiExceptionResponse> handleNotFoundException(ApiException e) {
    return createResponse(e, HttpStatus.NOT_FOUND);
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
