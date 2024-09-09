package online.talkandtravel.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.UserFacade;
import online.talkandtravel.model.entity.UserOnlineStatus;
import online.talkandtravel.service.UserService;
import online.talkandtravel.service.event.UserEventService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class UserFacadeImpl implements UserFacade {

  private final UserEventService userEventService;
  private final UserService userService;

  /**
   * Updates user online status, and notifies all subscribed users. Subscribe pattern - see doc
   * {@link UserEventService#publishUserOnlineStatusEvent}
   *
   * @param userOnlineStatus {@link UserOnlineStatus}
   * @param userId           user id
   */
  @Override
  public void updateUserOnlineStatusAndNotifyAll(UserOnlineStatus userOnlineStatus, Long userId) {
    log.info("update user online status with id:{}, isOnline:{}", userId,
        userOnlineStatus.isOnline());
    userService.updateUserOnlineStatus(userOnlineStatus);
    userEventService.publishUserOnlineStatusEvent(userOnlineStatus, userId);
  }
}
