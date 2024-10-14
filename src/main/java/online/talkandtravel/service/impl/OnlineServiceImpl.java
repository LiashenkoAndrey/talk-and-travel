package online.talkandtravel.service.impl;

import static online.talkandtravel.util.AuthenticationUtils.getUserFromPrincipal;
import static online.talkandtravel.util.RedisUtils.getUserLastSeenKey;
import static online.talkandtravel.util.RedisUtils.getUserLastSeenRedisKeys;
import static online.talkandtravel.util.RedisUtils.getUserStatusRedisKey;
import static online.talkandtravel.util.RedisUtils.getUserStatusRedisKeys;

import java.security.Principal;
import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.model.dto.user.OnlineStatusResponse;
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
    public void updateLastSeenOn(Long userId, ZonedDateTime lastSeenOn) {
        log.info("Update last seen on of user: {}", userId);
        String key = getUserLastSeenKey(userId);

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

    /**
     * Provides an initial snapshot of all user's online statuses
     *
     * @return a map where the key is the user ID and the value is a boolean indicating
     * whether the user is online (true) or offline (false)
     */
    @Override
    public Map<Long, OnlineStatusResponse> getAllUsersOnlineStatuses(List<Long> usersIdList) {
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
    public OnlineStatusResponse getUserOnlineStatusById(Long userId) {
        User user = getUser(userId);
        Map<Long, OnlineStatusResponse> statusDtoMap = getAllUsersOnlineStatusesForUsersIdList(List.of(user));
        return statusDtoMap.get(userId);
    }

    private OnlineStatusDto updateStatusToOffline(String key, Long userId) {
        ZonedDateTime lastSeenOn = ZonedDateTime.now(ZoneOffset.UTC);
        updateLastSeenOn(userId, lastSeenOn);
        redisTemplate.delete(key);
        return new OnlineStatusDto(userId, false, lastSeenOn);
    }

    private OnlineStatusDto updateStatusToOnline(String key, Long userId) {
        Duration keyDuration = Duration.ofSeconds(KEY_EXPIRATION_DURATION_IN_SEC);
        redisTemplate.opsForValue().set(key, Boolean.TRUE.toString(), keyDuration);
        return new OnlineStatusDto(userId, true);
    }

    private User getUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        return userOptional.orElseThrow(() ->  new UserNotFoundException(userId));
    }

    private Map<Long, OnlineStatusResponse> getAllUsersOnlineStatuses() {
        return getAllUsersOnlineStatusesForUsersIdList(userRepository.findAll());
    }

    public Map<Long, OnlineStatusResponse> getAllUsersOnlineStatusesForUsersList(List<Long> userIds) {
        return getAllUsersOnlineStatusesForUsersIdList(userRepository.findAllById(userIds));
    }

    public Map<Long, OnlineStatusResponse> getAllUsersOnlineStatusesForUsersIdList(List<User> users){
        List<Long> userIds = users
                .stream()
                .map(User::getId)
                .toList();

        List<String> userLastSeenRedisKeys = getUserLastSeenRedisKeys(userIds);
        List<String> userStatusRedisKeys = getUserStatusRedisKeys(userIds);

        List<Boolean> usersStatuses = getValuesAsBoolean(userStatusRedisKeys);
        List<ZonedDateTime> usersLastSeenData = getValuesAsTime(userLastSeenRedisKeys);

        return collectToMap(userIds, usersStatuses, usersLastSeenData);
    }

    private List<Boolean> getValuesAsBoolean(List<String> keys) {
        return getValuesFromKeys(keys)
                .stream()
                .map(Boolean::parseBoolean)
                .toList();
    }

    private List<ZonedDateTime> getValuesAsTime(List<String> keys) {
        return getValuesFromKeys(keys)
                .stream()
                .map((value) -> Optional.ofNullable(value)
                        .map(ZonedDateTime::parse)
                        .orElse(null))
                .toList();
    }

    private List<String> getValuesFromKeys(List<String> keys) {
        return Optional.ofNullable(redisTemplate.opsForValue().multiGet(keys))
                .orElse(List.of());
    }

    private Map<Long, OnlineStatusResponse> collectToMap(List<Long> userIdList,
        List<Boolean> usersStatuses, List<ZonedDateTime> usersLastSeenData) {
        Map<Long, OnlineStatusResponse> onlineStatuses = new HashMap<>(userIdList.size());

        IntStream.range(0, userIdList.size())
            .forEach((i) -> {
                Long userId = userIdList.get(i);
                Boolean isOnline = usersStatuses.get(i);
                ZonedDateTime lastSeenOn = usersLastSeenData.get(i);

                onlineStatuses.put(userId,
                    new OnlineStatusResponse(isOnline, lastSeenOn));
            });
        return onlineStatuses;
    }
}
