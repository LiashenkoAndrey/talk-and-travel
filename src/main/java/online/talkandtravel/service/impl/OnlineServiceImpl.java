package online.talkandtravel.service.impl;

import java.util.HashMap;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.service.OnlineService;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class OnlineServiceImpl implements OnlineService {

    private final RedisTemplate<String, String> redisTemplate;
    public static final String USER_STATUS_KEY = "user:%s:isOnline";
    public static final String USER_STATUS_KEY_PATTERN = "user:*:isOnline";

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
        return keys.stream().map((key) -> {
            // pattern - user:%s:isOnline
            String[] arr = key.split(":");
            return NumberUtils.toLong(arr[1]);
        }).toList();
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
