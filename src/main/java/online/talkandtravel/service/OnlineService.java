package online.talkandtravel.service;

import java.security.Principal;
import java.util.List;
import java.util.Map;

public interface OnlineService {

    void updateUserOnlineStatus(Principal principal, Boolean isOnline);

    /**
     * Notifies subscribed users that specific user updates their online status
     */
    void notifyUserOnlineStatusUpdated(Long userId, Boolean isOnline);


    /**
     * Provides an initial snapshot of all user's online statuses
     *
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    Map<Long, Boolean> getAllUsersOnlineStatuses();

    /**
     * Provides a current online status of a specific user
     *
     * @param userId user id
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    Boolean getUserOnlineStatusById(Long userId);

    /**
     * Retrieves the current online statuses of a specified list of users.
     *
     * @param userIds the list of user IDs for which to fetch online statuses
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    Map<Long, Boolean> getAllUsersOnlineStatusesForUsersList(List<Long> userIds);
}
