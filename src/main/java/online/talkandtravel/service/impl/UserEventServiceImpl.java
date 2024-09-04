package online.talkandtravel.service.impl;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.event.EventRequest;
import online.talkandtravel.model.enums.UserOnlineStatus;
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
  private static final Duration ONLINE_STATUS_EXPIRATION = Duration.ofMinutes(5);


  private static final String PUBLISH_EVENT_DESTINATION = "/countries/%s/messages";

  @Override
  public void updateUserOnlineStatus(UserOnlineStatus userOnlineStatus, Long userId) {
    String key = String.format(USER_STATUS_KEY, userId);
    redisTemplate.opsForValue().set(key, userOnlineStatus.isOnline(), ONLINE_STATUS_EXPIRATION);
  }

  @Override
  public void publishEvent(EventRequest request, Object payload) {
    messagingTemplate.convertAndSend(PUBLISH_EVENT_DESTINATION.formatted(request.chatId()), payload);
  }
}
