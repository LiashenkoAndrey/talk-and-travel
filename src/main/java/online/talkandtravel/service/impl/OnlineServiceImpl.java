package online.talkandtravel.service.impl;

import static online.talkandtravel.util.AuthenticationUtils.getUserFromPrincipal;

import java.security.Principal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.user.UserOnlineStatusDto;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.service.OnlineService;
import online.talkandtravel.util.RedisUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class OnlineServiceImpl implements OnlineService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    public static final String USER_STATUS_KEY = "user:%s:isOnline";
    public static final String USER_STATUS_KEY_PATTERN = "user:*:isOnline";

    public static final String USERS_ONLINE_STATUS_ENDPOINT = "/users/onlineStatus";

    @Value("${USER_ONLINE_STATUS_EXPIRATION_DURATION_IN_MIN}")
    public Long KEY_EXPIRATION_DURATION_IN_MIN;

    @Override
    public void updateUserOnlineStatus(Principal principal, Boolean isOnline) {
        User user = getUserFromPrincipal(principal);
        log.info("updateUserOnlineStatusById userId: {}, isOnline: {}", user.getId(), isOnline);
        String key = getRedisKey(user.getId());

        if (isOnline) {
            Duration keyDuration = Duration.ofMinutes(KEY_EXPIRATION_DURATION_IN_MIN);
            redisTemplate.opsForValue().set(key, isOnline.toString(), keyDuration);
        } else {
            redisTemplate.delete(key);
        }

        notifySubscribedUsers(user.getId(), isOnline);
    }

    @Override
    public void notifyUserOnlineStatusUpdated(Long userId, Boolean isOnline) {
        notifySubscribedUsers(userId, isOnline);
    }

    /**
     * Provides an initial snapshot of all user's online statuses
     *
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    @Override
    public Map<Long, Boolean> getAllUsersOnlineStatuses() {
        log.info("getAllUsersOnlineStatuses");

        Set<String> keys = redisTemplate.keys(USER_STATUS_KEY_PATTERN);
        List<Long> userIdList = getUserIdFromKeys(keys);
        List<String> values = redisTemplate.opsForValue().multiGet(keys);

        log.info("keys: {}, values: {}", keys, values);
        return mapKeysAndValuesToMap(userIdList, values);
    }

    /**
     * Provides a current online status of a specific user
     *
     * @param userId user id
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    @Override
    public Boolean getUserOnlineStatusById(Long userId) {
        String key = getRedisKey(userId);
        String value = redisTemplate.opsForValue().get(key);
        Boolean isOnline = Boolean.valueOf(value);

        log.info("getUserOnlineStatusById: {}, isOnline={}", userId, isOnline);
        return isOnline;
    }

    /**
     * Retrieves the current online statuses of a specified list of users.
     *
     * @param userIds the list of user IDs for which to fetch online statuses
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    @Override
    public Map<Long, Boolean> getAllUsersOnlineStatusesForUsersList(List<Long> userIds) {
        List<String> keys = getRedisKeys(userIds);
        List<String> values = redisTemplate.opsForValue().multiGet(keys);

        log.info("getAllUsersOnlineStatusesForUsersList {}", values);
        return  mapKeysAndValuesToMap(userIds, values);
    }

    private void notifySubscribedUsers(Long userId, Boolean isOnline) {
        UserOnlineStatusDto dto = new UserOnlineStatusDto(userId, isOnline);
        messagingTemplate.convertAndSend(USERS_ONLINE_STATUS_ENDPOINT, dto);
    }

    private Map<Long, Boolean> mapKeysAndValuesToMap(List<Long> userIdList, List<String> values) {
        Map<Long, Boolean> onlineStatuses = new HashMap<>(userIdList.size());

        int i = 0;
        for (Long userId : userIdList) {
            onlineStatuses.put(userId, Boolean.valueOf(values.get(i++)));
        }
        log.info("onlineStatuses map: {}", onlineStatuses);
        return onlineStatuses;
    }

    private List<Long> getUserIdFromKeys(Set<String> keys) {
        return keys.stream()
            .map(RedisUtils::getUserIdFromRedisKey)
            .toList();
    }

    public String getRedisKey(Long userId) {
        return String.format(USER_STATUS_KEY, userId);
    }

    public List<String> getRedisKeys(List<Long> usersIdList) {
        return usersIdList.stream()
            .map(this::getRedisKey)
            .toList();
    }
}
