package online.talkandtravel.exception.model;

import static online.talkandtravel.exception.util.ExceptionHandlerUtils.VALIDATION_FAILED_MESSAGE;
import static online.talkandtravel.util.AuthenticationUtils.getUserFromPrincipal;
import static online.talkandtravel.util.HttpUtils.createExceptionResponse;
import static online.talkandtravel.util.constants.ApiPathConstants.USER_WEBSOCKET_ERRORS_PATH;

import java.security.Principal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.util.ExceptionHandlerUtils;
import online.talkandtravel.model.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * Global exception handler for WebSocket exceptions.
 *
 * <p>Catches {@link WebSocketException} and sends an error message to the user via WebSocket.
 */
@Log4j2
@RequiredArgsConstructor
@ControllerAdvice
public class WebSocketExceptionHandler {

  private final SimpMessagingTemplate messagingTemplate;

  @MessageExceptionHandler(WebSocketException.class)
  public void handleWebSocketException(WebSocketException e) {
    ExceptionResponse errorMessage = createExceptionResponse(e.getMessage(), e.getHttpStatus(), e.getZonedDateTime());
    log.error("WebSocketException: {}", e.getMessage());

    sendErrorToUser(e.getUserId(), errorMessage);
  }

  /**
   * Handles exceptions thrown when method argument validation fails.
   */
  @MessageExceptionHandler(MethodArgumentNotValidException.class)
  public void handleArgumentValidationExceptions(MethodArgumentNotValidException exception, Principal principal) {
    String validationResults = Optional.ofNullable(exception.getBindingResult())
        .map(ExceptionHandlerUtils::getArgumentValidations)
        .orElse("Can't get a reason");

    User user = getUserFromPrincipal(principal);
    String destination = getDestination(exception.getFailedMessage());
    String message = VALIDATION_FAILED_MESSAGE + validationResults;

    ExceptionResponse errorMessage = badRequestError(message,  destination);

    log.info("validation exception: {}, message: {}", validationResults, exception.getMessage());
    sendErrorToUser(user.getId(), errorMessage);
  }

  @MessageExceptionHandler(MessageConversionException.class)
  public void handleMessageConversionException(Principal principal, MessageConversionException exception) {
    log.error("handleMessageConversionException, exception message: {}, principal {}",
        exception.getMessage(), principal);

    User user = getUserFromPrincipal(principal);
    String destination = getDestination(exception.getFailedMessage());
    String message = "Could not read JSON";

    ExceptionResponse errorMessage = badRequestError(message, destination);
    sendErrorToUser(user.getId(), errorMessage);
  }

  private ExceptionResponse badRequestError(String message, String destination) {
    return new ExceptionResponse(message, HttpStatus.BAD_REQUEST,
        ZonedDateTime.now(ZoneOffset.UTC), destination);
  }

  private String getDestination(@Nullable Message<?> failedMessage) {
    if (failedMessage == null) {
      return null;
    }

    MessageHeaders headers = failedMessage.getHeaders();
    Object nativeHeaders = headers.get("nativeHeaders");

    if (nativeHeaders == null) {
      return null;
    }

    if (nativeHeaders instanceof LinkedMultiValueMap<?, ?> map) {
      List<?> destinations = map.get("destination");
      if (destinations != null && !destinations.isEmpty()) {
        return destinations.toString();
      }
    }
    return null;
  }

  private void sendErrorToUser(Long userId, ExceptionResponse response) {
    messagingTemplate.convertAndSend(USER_WEBSOCKET_ERRORS_PATH.formatted(userId), response);
  }
}
