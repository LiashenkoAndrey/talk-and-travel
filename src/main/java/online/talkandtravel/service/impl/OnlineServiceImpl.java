package online.talkandtravel.service.impl;

import static online.talkandtravel.util.AuthenticationUtils.getUserFromPrincipal;
import static online.talkandtravel.util.RedisUtils.USER_STATUS_KEY_PATTERN;
import static online.talkandtravel.util.RedisUtils.getUserLastSeenRedisKeys;
import static online.talkandtravel.util.RedisUtils.getUserStatusRedisKey;
import static online.talkandtravel.util.RedisUtils.getUserStatusRedisKeys;
import static online.talkandtravel.util.RedisUtils.getUserIdFromKeys;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
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
    public void updateLastSeenOn(Long userId, LocalDateTime lastSeenOn) {
        String key = getUserStatusRedisKey(userId);

        redisTemplate.opsForValue().set(key, lastSeenOn.toString());
    }


    @Override
    public OnlineStatusDto updateUserOnlineStatus(Principal principal, Boolean isOnline) {
        User user = getUserFromPrincipal(principal);
        return updateUserOnlineStatus(user.getId(), isOnline);
    }

    @Override
    public OnlineStatusDto updateUserOnlineStatus(Long userId, Boolean isOnline) {
        log.info("updateUserOnlineStatusById userId: {}, isOnline: {}", userId, isOnline);
        String key = getUserStatusRedisKey(userId);

        return isOnline ?
            updateStatusToOnline(key, userId)
                :
            updateStatusToOffline(key, userId);
    }

    private OnlineStatusDto updateStatusToOffline(String key, Long userId) {
        LocalDateTime lastSeenOn = LocalDateTime.now();
        updateLastSeenOn(userId, lastSeenOn);
        redisTemplate.delete(key);
        return new OnlineStatusDto(userId, false, lastSeenOn);
    }

    private OnlineStatusDto updateStatusToOnline(String key, Long userId) {
        Duration keyDuration = Duration.ofSeconds(KEY_EXPIRATION_DURATION_IN_SEC);
        redisTemplate.opsForValue().set(key, Boolean.TRUE.toString(), keyDuration);
        return new OnlineStatusDto(userId, true);
    }

    /**
     * Provides an initial snapshot of all user's online statuses
     *
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    @Override
    public Map<Long, OnlineStatusDto> getAllUsersOnlineStatuses(List<Long> usersIdList) {
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
        String key = getUserStatusRedisKey(userId);
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

    private Map<Long, OnlineStatusDto> getAllUsersOnlineStatuses() {
        Set<String> keysSet = Optional.ofNullable(redisTemplate.keys(USER_STATUS_KEY_PATTERN))
                .orElse(Set.of());
        List<String> keys = List.copyOf(keysSet);
        List<Long> userIdList = getUserIdFromKeys(keys);
        List<Boolean> values = getValuesFromKeys(keys);

        List<User> users = userRepository.findAll();
        Map<Long, Boolean> mapFromRedis = mapKeysAndValuesToMap(userIdList, values);
        Map<Long, Boolean> allUsersMap = new HashMap<>(users.size());

        users.forEach((user) -> allUsersMap.put(user.getId(),
            mapFromRedis.getOrDefault(user.getId(), false)));
        return allUsersMap;
    }

    /**
     * Retrieves the current online statuses of a specified list of users.
     *
     * @param userIds the list of user IDs for which to fetch online statuses
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    public Map<Long, OnlineStatusDto> getAllUsersOnlineStatusesForUsersList(List<Long> userIds) {
        List<Long> realUsersIds = userRepository.findAllById(userIds)
                .stream()
                .map(User::getId)
                .toList();

        List<String> userLastSeenRedisKeys = getUserLastSeenRedisKeys(realUsersIds);
        List<String> userStatusRedisKeys = getUserStatusRedisKeys(realUsersIds);

        List<Boolean> usersStatuses = getValuesFromKeys(userStatusRedisKeys, Boolean.class);
        List<LocalDateTime> usersLastSeenData = getValuesFromKeys(userLastSeenRedisKeys, LocalDateTime.class);

        return mapKeysAndValuesToMap(realUsersIds, usersStatuses, usersLastSeenData);
    }

    private <T> List<T> getValuesFromKeys(List<String> keys, Class<T> classType) {
        List<String> stringValues = Optional.ofNullable(redisTemplate.opsForValue().multiGet(keys))
                .orElse(List.of());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        return stringValues.stream()
                .map((value) -> readVal(value, classType))
                .toList();
    }

    private <T> T readVal(String stringValues, Class<T> classType) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            return mapper.readValue(stringValues, classType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<Long, OnlineStatusDto> mapKeysAndValuesToMap(List<Long> userIdList,
        List<Boolean> usersStatuses, List<LocalDateTime> usersLastSeenData) {
        Map<Long, OnlineStatusDto> onlineStatuses = new HashMap<>(userIdList.size());

        IntStream.range(0, userIdList.size())
            .forEach((i) -> {
                Long userId = userIdList.get(i);
                Boolean isOnline = usersStatuses.get(i);
                LocalDateTime lastSeenOn = usersLastSeenData.get(i);

                onlineStatuses.put(userIdList.get(i),
                    new OnlineStatusDto(userId, isOnline, lastSeenOn));
            });

        return onlineStatuses;
    }
}
