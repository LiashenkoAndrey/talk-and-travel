package online.talkandtravel.exception.model;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    messagingTemplate.convertAndSendToUser(e.getUserId().toString(), "/errors", errorMessage);
  }
}
