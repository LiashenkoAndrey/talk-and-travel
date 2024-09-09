package online.talkandtravel.facade;

import online.talkandtravel.model.entity.UserOnlineStatus;

public interface UserFacade {

  /**
   * Updates user online status, and notifies all subscribed users. Subscribe pattern - see doc
   * {@link online.talkandtravel.service.event.UserEventService#publishUserOnlineStatusEvent}
   *
   * @param userOnlineStatus {@link online.talkandtravel.model.entity.UserOnlineStatus}
   * @param userId           user id
   */
  void updateUserOnlineStatusAndNotifyAll(UserOnlineStatus userOnlineStatus, Long userId);

}
