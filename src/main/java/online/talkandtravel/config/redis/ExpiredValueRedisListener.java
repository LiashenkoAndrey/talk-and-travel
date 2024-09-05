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

@Component
@Log4j2
@RequiredArgsConstructor
public class ExpiredValueRedisListener implements MessageListener {

  private final UserEventService userEventService;

  @Override
  public void onMessage(Message message, byte[] pattern) {
    String key = new String(message.getBody());
    log.info("expired key: {}", key);
    Long userId = getUserIdFromRedisKey(key);
    userEventService.publishEvent(UserOnlineStatus.OFFLINE, userId);
  }

  /**
   * parses redis key
   * user online status key pattern -- user:{userId}:isOnline
   * @return userId
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
