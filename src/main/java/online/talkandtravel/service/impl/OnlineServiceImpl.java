package online.talkandtravel.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.service.OnlineService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class OnlineServiceImpl implements OnlineService {

    private final RedisTemplate<String, Boolean> redisTemplate;
    public static final String USER_STATUS_KEY = "user:%s:isOnline";

    /**
     * Provides an initial snapshot of all user's online statuses
     *
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    @Override
    public Map<Long, Boolean> getAllUsersOnlineStatuses() {
        log.info("getAllUsersOnlineStatuses");
        return Map.of();
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
        Boolean isOnline = redisTemplate.opsForValue().get(key);

        log.info("getUserOnlineStatusById: {}, isOnline={}", userId, isOnline);
        return isOnline != null ? isOnline : false;
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
        log.info("getAllUsersOnlineStatusesForUsersList {}", userIds);
        return Map.of();
    }

    public static String getRedisKey(Long userId) {
        return String.format(USER_STATUS_KEY, userId);
    }
}
