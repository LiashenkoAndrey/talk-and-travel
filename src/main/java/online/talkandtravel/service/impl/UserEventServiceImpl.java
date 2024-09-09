package online.talkandtravel.service.impl;


import static online.talkandtravel.util.service.EventDestination.USER_ONLINE_STATUS_DESTINATION;
import static online.talkandtravel.util.service.EventDestination.USER_STATUS_KEY;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.user.UserOnlineStatusDto;
import online.talkandtravel.model.entity.UserOnlineStatus;
import online.talkandtravel.service.event.UserEventService;
import online.talkandtravel.util.service.EventPublisherUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserEventServiceImpl implements UserEventService {

  private final RedisTemplate<String, String> redisTemplate;

  private static final String USER_STATUS_KEY = "user:%s:isOnline";
  private final EventPublisherUtil publisherUtil;

  @Override
  public void updateUserOnlineStatusAndNotifyAll(UserOnlineStatus userOnlineStatus, Long userId) {
    log.info("update user online status with id:{}, isOnline:{}", userId, userOnlineStatus.isOnline());
    try {
      String key = String.format(USER_STATUS_KEY, userId);
      String isOnline = userOnlineStatus.isOnline().toString();
      Duration expirationDuration = publisherUtil.getUserOnlineStatusExpirationDuration();

      redisTemplate.opsForValue().set(key, isOnline, expirationDuration);
      publishEvent(userOnlineStatus, userId);
    } catch (Exception e) {
      log.error("updateUserOnlineStatus: " + e.getMessage());
    }
  }

  @Override
  public void publishEvent(UserOnlineStatus isOnline, Long userId) {
    String dest = USER_ONLINE_STATUS_DESTINATION.formatted(userId);
    log.info("publishEvent: payload: {}, dest: {}", isOnline.isOnline(), dest);
    publisherUtil.publishEvent(dest, new UserOnlineStatusDto(userId, isOnline.isOnline()));
  }
}
