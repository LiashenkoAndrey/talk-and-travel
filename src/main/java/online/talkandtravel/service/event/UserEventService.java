package online.talkandtravel.service.event;

import online.talkandtravel.model.entity.UserOnlineStatus;

/**
 * Service interface for managing user events
 */
public interface UserEventService {

  /**
   * Updates user online status, and notifies all subscribed users.
   * Subscribe pattern - see doc {@link online.talkandtravel.service.event.UserEventService#publishEvent}
   * @param userOnlineStatus {@link online.talkandtravel.model.entity.UserOnlineStatus}
   * @param userId user id
   */
  void updateUserOnlineStatusAndNotifyAll(UserOnlineStatus userOnlineStatus, Long userId);

  /**
   * Notifies all subscribed users
   * Subscribe pattern - {@value online.talkandtravel.util.service.EventDestination#USER_ONLINE_STATUS_DESTINATION}
   * @param userOnlineStatus {@link online.talkandtravel.model.entity.UserOnlineStatus}
   * @param userId user id
   */
  void publishEvent(UserOnlineStatus userOnlineStatus, Long userId);
}
