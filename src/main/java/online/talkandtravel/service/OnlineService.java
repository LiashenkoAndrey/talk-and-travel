package online.talkandtravel.service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import online.talkandtravel.model.dto.user.OnlineStatusDto;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import online.talkandtravel.model.dto.user.OnlineStatusResponse;

public interface OnlineService {

    void updateLastSeenOn(Long userId, LocalDateTime lastSeenOn);

    OnlineStatusDto updateUserOnlineStatus(Principal principal, Boolean isOnline);

    OnlineStatusDto updateUserOnlineStatus(Long userId, Boolean isOnline);

    /**
     * Provides an initial snapshot of all user's online statuses
     *
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    Map<Long, OnlineStatusResponse> getAllUsersOnlineStatuses(List<Long> usersIdList);

    /**
     * Provides a current online status of a specific user
     *
     * @param userId user id
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    OnlineStatusResponse getUserOnlineStatusById(Long userId);
}
