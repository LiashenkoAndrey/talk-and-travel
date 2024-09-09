package online.talkandtravel.config.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.util.StringParseException;
import online.talkandtravel.model.entity.UserOnlineStatus;
import online.talkandtravel.service.event.UserEventService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

/**
 * This class handles events triggered when a key expires. Upon expiration, it publishes an event
 * indicating that the user's online status has been updated to {@code UserOnlineStatus.OFFLINE}.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class ExpiredValueRedisListener implements MessageListener {

  private final UserEventService userEventService;

  /**
   * Invoked when a Redis key expires. This method processes the expired key
   * and updates the user's online status to OFFLINE by publishing an event.
   *
   * @param message the message containing the expired key details
   * @param pattern the pattern used for key expiration events (not used in this method)
   */
  @Override
  public void onMessage(Message message, byte[] pattern) {
    String key = new String(message.getBody());
    Long userId = getUserIdFromRedisKey(key);
    userEventService.publishUserOnlineStatusEvent(UserOnlineStatus.OFFLINE, userId);
  }

  /**
   * Extracts the user ID from the Redis key following the pattern:
   * "user:{userId}:isOnline".
   *
   * @param messageBody the body of the Redis key message
   * @return the extracted user ID as a Long
   * @throws StringParseException if the user ID cannot be parsed as a long value
   */
  private Long getUserIdFromRedisKey(String messageBody) {
    String[] array = messageBody.split(":");
    String userId = array[1]; //see pattern in java doc
    if (NumberUtils.isParsable(userId)) {
      return NumberUtils.toLong(userId);
    }
    throw new StringParseException(userId, "Can't parse a long value");
  }
}
