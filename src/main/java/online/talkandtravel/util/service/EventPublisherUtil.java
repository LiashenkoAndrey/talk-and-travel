package online.talkandtravel.util.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * Utility class for publishing events to WebSocket destinations and managing user-related event
 * timing. This class provides methods for publishing events via a messaging template.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class EventPublisherUtil {

  private final SimpMessagingTemplate messagingTemplate;

  @Value("${USER_ONLINE_STATUS_EXPIRATION_DURATION_IN_MIN}")
  private Long userOnlineStatusExpirationDuration;

  /**
   * Retrieves the expiration duration for user online status in seconds, as configured in the
   * application properties.
   *
   * @return the expiration duration as a {@link Duration}
   */
  public Duration getUserOnlineStatusExpirationDuration() {
    return Duration.ofSeconds(userOnlineStatusExpirationDuration);
  }

  /**
   * Publishes an event to the specified WebSocket destination. Converts the provided payload to a
   * message and sends it to the given destination.
   *
   * @param destination the WebSocket destination where the event will be published
   * @param payload     the object to be sent as the message payload
   */
  public void publishEvent(String destination, Object payload) {
    try {
      messagingTemplate.convertAndSend(destination, payload);
    } catch (Exception e) {
      // Handle potential errors in event publishing (e.g., WebSocket failures)
      log.error("Error while publishing event to {}: {}", destination, e.getMessage());
    }
  }
}