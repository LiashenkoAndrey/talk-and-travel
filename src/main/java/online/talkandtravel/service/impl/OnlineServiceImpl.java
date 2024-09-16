package online.talkandtravel.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.OnlineService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Duration;
import java.util.*;
import java.util.stream.IntStream;

import static online.talkandtravel.util.AuthenticationUtils.getUserFromPrincipal;
import static online.talkandtravel.util.RedisUtils.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class OnlineServiceImpl implements OnlineService {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    public static final String USERS_ONLINE_STATUS_ENDPOINT = "/users/onlineStatus";

    @Value("${USER_ONLINE_STATUS_EXPIRATION_DURATION_IN_MIN}")
    public Long KEY_EXPIRATION_DURATION_IN_MIN;

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
            Duration keyDuration = Duration.ofMinutes(KEY_EXPIRATION_DURATION_IN_MIN);
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

    @Override
    public void notifyUserOnlineStatusUpdated(Long userId, Boolean isOnline) {
        OnlineStatusDto dto = new OnlineStatusDto(userId, isOnline);
        messagingTemplate.convertAndSend(USERS_ONLINE_STATUS_ENDPOINT, dto);
    }

    private void checkUserExists(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            log.error("user with id: {} not found", userId);
            throw new UserNotFoundException(userId);
        }
    }

    private Map<Long, Boolean> getAllUsersOnlineStatuses() {
        log.info("getAllUsersOnlineStatuses");
        Set<String> keys = Optional.ofNullable(redisTemplate.keys(USER_STATUS_KEY_PATTERN))
                .orElse(Set.of());
        List<Long> userIdList = getUserIdFromKeys(keys);
        List<Boolean> values = getValuesFromKeys(keys);

        List<User> users = userRepository.findAll();
        Map<Long, Boolean> mapFromRedis = mapKeysAndValuesToMap(userIdList, values);
        Map<Long, Boolean> allUsersMap = new HashMap<>(users.size());

        users.forEach(user -> allUsersMap.put(user.getId(), mapFromRedis.getOrDefault(user.getId(), false)));
        return allUsersMap;
    }

    /**
     * Retrieves the current online statuses of a specified list of users.
     *
     * @param userIds the list of user IDs for which to fetch online statuses
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    private Map<Long, Boolean> getAllUsersOnlineStatusesForUsersList(List<Long> userIds) {
        List<Long> realUsersIds = userRepository.findAllById(userIds)
                .stream()
                .map(User::getId)
                .toList();
        List<String> keys = getRedisKeys(realUsersIds);
        List<Boolean> values = getValuesFromKeys(keys);

        return mapKeysAndValuesToMap(realUsersIds, values);
    }

    private List<Boolean> getValuesFromKeys(Collection<String> keys) {
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
