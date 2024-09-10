package online.talkandtravel.facade.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.UserFacade;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserOnlineStatus;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.UserService;
import online.talkandtravel.service.event.UserEventService;
import online.talkandtravel.util.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class UserFacadeImpl implements UserFacade {

  private final UserEventService userEventService;
  private final UserService userService;
  private final AuthenticationService authenticationService;

  /**
   * Updates the online status of a user and notifies all subscribed users about the change. This
   * follows the publish-subscribe pattern, where users subscribed to updates will be notified when
   * the online status changes.
   *
   * @param userOnlineStatus the current online status of the user, represented by
   *                         {@link online.talkandtravel.model.entity.UserOnlineStatus}
   */
  @Override
  public void updateUserOnlineStatusAndNotifyAll(UserOnlineStatus userOnlineStatus) {
    User user = authenticationService.getAuthenticatedUser();
    log.info("update user online status with id:{}, isOnline:{}", user.getId(),
        userOnlineStatus.isOnline());
    userService.updateUserOnlineStatus(userOnlineStatus, user);
    userEventService.publishUserOnlineStatusEvent(userOnlineStatus, user.getId());
  }
}
