package online.talkandtravel.service.impl;


import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.event.EventRequest;
import online.talkandtravel.model.entity.UserOnlineStatus;
import online.talkandtravel.service.event.UserEventService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserEventServiceImpl implements UserEventService {

  private final SimpMessagingTemplate messagingTemplate;
  private final RedisTemplate<String, String> redisTemplate;

  private static final String USER_STATUS_KEY = "user:%s:isOnline";
  private static final Duration ONLINE_STATUS_EXPIRATION = Duration.ofSeconds(5);
  private static final String PUBLISH_EVENT_DESTINATION = "/user/%s/onlineStatus";


  @Override
  public void updateUserOnlineStatus(UserOnlineStatus userOnlineStatus, Long userId) {
    log.info("update user online status with id:{}, isOnline:{}", userId, userOnlineStatus.isOnline());
    try {
      String key = String.format(USER_STATUS_KEY, userId);
      redisTemplate.opsForValue().set(key, userOnlineStatus.isOnline().toString(), ONLINE_STATUS_EXPIRATION);
      publishEvent(userOnlineStatus, userId);
    } catch (Exception e) {
      log.error("updateUserOnlineStatus: " + e.getMessage());
    }
  }

  @Override
  public void publishEvent(UserOnlineStatus isOnline, Long userId) {
    log.info("publishEvent: payload: {}", isOnline.isOnline());
    messagingTemplate.convertAndSend(PUBLISH_EVENT_DESTINATION.formatted(userId), isOnline.isOnline());
  }
}
