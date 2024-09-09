package online.talkandtravel.service.event;

import online.talkandtravel.model.entity.UserOnlineStatus;

/**
 * Service interface for handling and publishing user-related events.
 * This service is responsible for notifying users about changes in user status or activities.
 */
public interface UserEventService {


  /**
   * Publishes an event notifying all subscribed users about the online status of a specific user.
   * This follows the publish-subscribe pattern, where subscribers receive updates when a user's
   * online status changes.
   *
   * @param userOnlineStatus the current online status of the user, represented by
   *                         {@link online.talkandtravel.model.entity.UserOnlineStatus}
   * @param userId           the ID of the user whose online status has changed
   */
  void publishUserOnlineStatusEvent(UserOnlineStatus userOnlineStatus, Long userId);
}
