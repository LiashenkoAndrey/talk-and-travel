package online.talkandtravel.service.event;

import online.talkandtravel.model.entity.UserOnlineStatus;

/**
 * Service interface for managing user events
 */
public interface UserEventService {


  /**
   * Notifies all subscribed users
   * Subscribe pattern - {@value online.talkandtravel.util.service.EventDestination#USER_ONLINE_STATUS_DESTINATION}
   * @param userOnlineStatus {@link online.talkandtravel.model.entity.UserOnlineStatus}
   * @param userId user id
   */
  void publishUserOnlineStatusEvent(UserOnlineStatus userOnlineStatus, Long userId);
}
