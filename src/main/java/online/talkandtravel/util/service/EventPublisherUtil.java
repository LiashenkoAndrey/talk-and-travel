package online.talkandtravel.util.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class EventPublisherUtil {

  private final SimpMessagingTemplate messagingTemplate;

  @Value("${USER_ONLINE_STATUS_EXPIRATION_DURATION_IN_MIN}")
  private Long userOnlineStatusExpirationDuration;

  public Duration getUserOnlineStatusExpirationDuration() {
    return Duration.ofSeconds(userOnlineStatusExpirationDuration);
  }

  public void publishEvent(String destination, Object payload) {
    try {
      messagingTemplate.convertAndSend(destination, payload);
    } catch (Exception e) {
      // Handle potential errors in event publishing (e.g., WebSocket failures)
      log.error("Error while publishing event to {}: {}", destination, e.getMessage());
    }
  }
}