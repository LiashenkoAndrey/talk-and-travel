package online.talkandtravel.facade;

import online.talkandtravel.model.entity.UserOnlineStatus;

/**
 * Facade interface for handling user-related operations and interactions. This facade provides
 * methods to manage user states and to notify other users about changes.
 */
public interface UserFacade {

  /**
   * Updates the online status of a user and notifies all subscribed users about the change. This
   * follows the publish-subscribe pattern, where users subscribed to updates will be notified when
   * the online status changes.
   *
   * @param userOnlineStatus the current online status of the user, represented by
   *                         {@link online.talkandtravel.model.entity.UserOnlineStatus}
   */
  void updateUserOnlineStatusAndNotifyAll(UserOnlineStatus userOnlineStatus);

}
