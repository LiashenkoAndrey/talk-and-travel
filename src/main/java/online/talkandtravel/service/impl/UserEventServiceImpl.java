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

  /**
   * Publishes an event notifying all subscribed users about the online status of a specific user.
   * This follows the publish-subscribe pattern, where subscribers receive updates when a user's
   * online status changes.
   *
   * @param userOnlineStatus the current online status of the user, represented by
   *                         {@link online.talkandtravel.model.entity.UserOnlineStatus}
   * @param userId           the ID of the user whose online status has changed
   */
  @Override
  public void publishUserOnlineStatusEvent(UserOnlineStatus userOnlineStatus, Long userId) {
    String dest = USER_ONLINE_STATUS_DESTINATION.formatted(userId);
    log.info("publishEvent: payload: {}, userId: {}, dest: {}", userOnlineStatus.isOnline(),
        userId, dest);
    publisherUtil.publishEvent(dest, new UserOnlineStatusDto(userId, userOnlineStatus.isOnline()));
  }
}
