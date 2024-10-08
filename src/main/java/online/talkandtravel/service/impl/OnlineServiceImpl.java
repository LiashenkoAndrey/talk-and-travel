package online.talkandtravel.service.impl;

import static online.talkandtravel.util.AuthenticationUtils.getUserFromPrincipal;
import static online.talkandtravel.util.RedisUtils.USER_STATUS_KEY_PATTERN;
import static online.talkandtravel.util.RedisUtils.getRedisKey;
import static online.talkandtravel.util.RedisUtils.getRedisKeys;
import static online.talkandtravel.util.RedisUtils.getUserIdFromKeys;

import java.security.Principal;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.OnlineService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class OnlineServiceImpl implements OnlineService {

    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    @Value("${USER_ONLINE_STATUS_EXPIRATION_DURATION_IN_SEC}")
    public Long KEY_EXPIRATION_DURATION_IN_SEC;

    @Override
    public OnlineStatusDto updateUserOnlineStatus(Principal principal, Boolean isOnline) {
        User user = getUserFromPrincipal(principal);
        return updateUserOnlineStatus(user.getId(), isOnline);
    }

    @Override
    public OnlineStatusDto updateUserOnlineStatus(Long userId, Boolean isOnline) {
        log.info("updateUserOnlineStatusById userId: {}, isOnline: {}", userId, isOnline);
        String key = getRedisKey(userId);

        if (isOnline) {
            Duration keyDuration = Duration.ofSeconds(KEY_EXPIRATION_DURATION_IN_SEC);
            redisTemplate.opsForValue().set(key, isOnline.toString(), keyDuration);
            return new OnlineStatusDto(userId, true);
        } else {
            redisTemplate.delete(key);
            return new OnlineStatusDto(userId, false);
        }
    }

    /**
     * Provides an initial snapshot of all user's online statuses
     *
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    @Override
    public Map<Long, Boolean> getAllUsersOnlineStatuses(List<Long> usersIdList) {
        if (usersIdList != null && !usersIdList.isEmpty()) {
            return getAllUsersOnlineStatusesForUsersList(usersIdList);
        }
        return getAllUsersOnlineStatuses();
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
        checkUserExists(userId);
        String key = getRedisKey(userId);
        String value = redisTemplate.opsForValue().get(key);
        Boolean isOnline = Boolean.valueOf(value);

        log.info("getUserOnlineStatusById: {}, isOnline={}", userId, isOnline);
        return isOnline;
    }

    private void checkUserExists(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.error("user with id: {} not found", userId);
            throw new UserNotFoundException(userId);
        }
    }

    private Map<Long, Boolean> getAllUsersOnlineStatuses() {
        Set<String> keysSet = Optional.ofNullable(redisTemplate.keys(USER_STATUS_KEY_PATTERN))
                .orElse(Set.of());
        List<String> keys = List.copyOf(keysSet);
        List<Long> userIdList = getUserIdFromKeys(keys);
        List<Boolean> values = getValuesFromKeys(keys);

        List<User> users = userRepository.findAll();
        Map<Long, Boolean> mapFromRedis = mapKeysAndValuesToMap(userIdList, values);
        Map<Long, Boolean> allUsersMap = new HashMap<>(users.size());

        users.forEach((user) -> allUsersMap.put(user.getId(), mapFromRedis.getOrDefault(user.getId(), false)));
        return allUsersMap;
    }

    /**
     * Retrieves the current online statuses of a specified list of users.
     *
     * @param userIds the list of user IDs for which to fetch online statuses
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    public Map<Long, Boolean> getAllUsersOnlineStatusesForUsersList(List<Long> userIds) {
        List<Long> realUsersIds = userRepository.findAllById(userIds)
                .stream()
                .map(User::getId)
                .toList();
        List<String> keys = getRedisKeys(realUsersIds);
        List<Boolean> values = getValuesFromKeys(keys);

        return mapKeysAndValuesToMap(realUsersIds, values);
    }

    private List<Boolean> getValuesFromKeys(List<String> keys) {
        List<String> stringValues = Optional.ofNullable(redisTemplate.opsForValue().multiGet(keys))
                .orElse(List.of());
        return stringValues.stream()
                .map(Boolean::valueOf)
                .toList();
    }

    private Map<Long, Boolean> mapKeysAndValuesToMap(List<Long> userIdList, List<Boolean> values) {
        Map<Long, Boolean> onlineStatuses = new HashMap<>(userIdList.size());
        IntStream.range(0, userIdList.size())
                .forEach((i) -> onlineStatuses.put(userIdList.get(i), values.get(i)));

        return onlineStatuses;
    }
}
