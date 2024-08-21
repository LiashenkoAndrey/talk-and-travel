package online.talkandtravel.exception.websocket;

import org.springframework.messaging.Message;

public class MessageDeliveryException extends org.springframework.messaging.MessageDeliveryException {

  private static final String MESSAGE = "Error during connection. Authentication failed. ";



  public MessageDeliveryException(String description) {
    super(description);
  }

  public MessageDeliveryException(Message<?> undeliveredMessage) {
    super(undeliveredMessage);
  }

  public MessageDeliveryException(Message<?> undeliveredMessage, String description) {
    super(undeliveredMessage, description);
  }

  public MessageDeliveryException(Message<?> message, Throwable cause) {
    super(message, MESSAGE, cause);
  }

  public MessageDeliveryException(Message<?> undeliveredMessage, String description,
      Throwable cause) {
    super(undeliveredMessage, MESSAGE + description, cause);
  }
}
