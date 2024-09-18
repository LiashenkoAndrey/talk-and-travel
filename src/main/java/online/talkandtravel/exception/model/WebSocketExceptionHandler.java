package online.talkandtravel.exception.model;

import static online.talkandtravel.util.AuthenticationUtils.getUserFromPrincipal;

import java.security.Principal;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import online.talkandtravel.model.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * Global exception handler for WebSocket exceptions.
 *
 * <p>Catches {@link WebSocketException} and sends an error message to the user via WebSocket.
 */
@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class WebSocketExceptionHandler {

  private final SimpMessagingTemplate messagingTemplate;

  @MessageExceptionHandler(WebSocketException.class)
  public void handleWebSocketException(WebSocketException e) {
    ExceptionResponse errorMessage =
        new ExceptionResponse(e.getMessage(), e.getHttpStatus(), e.getZonedDateTime());
    log.error(e.getMessageToClient());
    sendErrorToUser(e.getUserId(), errorMessage);
  }

  @MessageExceptionHandler(MessageConversionException.class)
  public void handleMessageConversionException(Principal principal, MessageConversionException exception) {
    log.error("handleMessageConversionException, exception message: {}, principal {}", exception.getMessage(), principal);
    User user = getUserFromPrincipal(principal);
    String message = "Could not read JSON";
    ExceptionResponse errorMessage =
        new ExceptionResponse(message, HttpStatus.BAD_REQUEST, ZonedDateTime.now());
    sendErrorToUser(user.getId(), errorMessage);
  }

  private void sendErrorToUser(Long userId, ExceptionResponse response) {
    messagingTemplate.convertAndSendToUser(userId.toString(), "/errors", response);
  }
}
