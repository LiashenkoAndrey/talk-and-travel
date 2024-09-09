package online.talkandtravel.service.impl;


import static online.talkandtravel.util.service.EventDestination.USER_ONLINE_STATUS_DESTINATION;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.user.UserOnlineStatusDto;
import online.talkandtravel.model.entity.UserOnlineStatus;
import online.talkandtravel.service.event.UserEventService;
import online.talkandtravel.util.service.EventPublisherUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserEventServiceImpl implements UserEventService {

  private final EventPublisherUtil publisherUtil;

  @Override
  public void publishUserOnlineStatusEvent(UserOnlineStatus isOnline, Long userId) {
    String dest = USER_ONLINE_STATUS_DESTINATION.formatted(userId);
    log.info("publishEvent: payload: {}, userId: {}, dest: {}", isOnline.isOnline(), userId, dest);
    publisherUtil.publishEvent(dest, new UserOnlineStatusDto(userId, isOnline.isOnline()));
  }
}
