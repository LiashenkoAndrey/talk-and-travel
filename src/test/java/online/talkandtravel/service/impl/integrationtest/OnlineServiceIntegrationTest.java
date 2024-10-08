package online.talkandtravel.service.impl.integrationtest;

import static online.talkandtravel.config.TestDataConstant.USERS_DATA_SQL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.config.IntegrationTest;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.service.OnlineService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.jdbc.Sql;

@Log4j2
@Sql({USERS_DATA_SQL})
public class OnlineServiceIntegrationTest extends IntegrationTest {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;


  @Autowired
  private RedisConnectionFactory redisConnectionFactory;

  @Autowired
  private OnlineService underTest;

  @Value("${USER_ONLINE_STATUS_EXPIRATION_DURATION_IN_SEC}")
  Long redisKeyExpirationInSec;

  private static final String REDIS_KEY_TEMPLATE = "user:%s:isOnline";

  private static final Long NOT_EXISTING_USER_ID = 777L;

  @Nested
  class UpdateUserOnlineStatus {

    private final Long USER_ID = 1L;
    private final String REDIS_KEY = createRedisKeyByUserId(USER_ID);

    @Test
    void shouldUpdateStatusToOnlineAndSetKeyExpirationTime() {

      OnlineStatusDto result = underTest.updateUserOnlineStatus(USER_ID, true);

      assertEquals(true, result.isOnline());
      assertEquals(USER_ID, result.userId());
      assertEquals(true, redisTemplate.expire(REDIS_KEY, Duration.ofSeconds(redisKeyExpirationInSec)));

      String onlineStatusInRedis = redisTemplate.opsForValue().get(REDIS_KEY);
      assertNotNull(onlineStatusInRedis, "Online status should be stored in Redis");
      assertEquals("true", onlineStatusInRedis, "Online status in Redis should be 'true'");
    }

    @Test
    void shouldUpdateStatusToOffline() {
      OnlineStatusDto result = underTest.updateUserOnlineStatus(USER_ID, false);

      assertEquals(false, result.isOnline());
      assertEquals(USER_ID, result.userId());
      assertEquals(false, redisTemplate.hasKey(REDIS_KEY));
    }


  }
  @Nested
  class GetAllUsersOnlineStatuses {

    @AfterEach
    void clearRedis() {
      try (RedisConnection connection = RedisConnectionUtils.getConnection(redisConnectionFactory)) {
        connection.serverCommands().flushDb();
      }
    }

//    @ParameterizedTest
//    @MethodSource("emptyListOrNullArgs")
//    void shouldGetAllOfflineUsers_whenEmptyListOrNull(List<Long> usersIdList ) {
//      Map<Long, Boolean> actualMap = underTest.getAllUsersOnlineStatuses(usersIdList);
//
//      assertThat(actualMap).hasEntrySatisfying(2L, (value) -> assertThat(value).isFalse());
//      assertThat(actualMap).hasEntrySatisfying(3L, (value) -> assertThat(value).isFalse());
//    }
//
//
//
//    @ParameterizedTest
//    @MethodSource("emptyListOrNullArgs")
//    void shouldGetOnlineUsers_whenEmptyListOrNullAndHasOnlineUsers(List<Long> usersIdList ) {
//      setUserStatusToOnlineById(2L);
//
//      Map<Long, Boolean> actualMap = underTest.getAllUsersOnlineStatuses(usersIdList);
//
//      assertThat(actualMap).hasEntrySatisfying(2L, (value) -> assertThat(value).isTrue());
//      assertThat(actualMap).hasEntrySatisfying(3L, (value) -> assertThat(value).isFalse());
//    }
//
//    @ParameterizedTest
//    @MethodSource("notEmptyListArgs")
//    void shouldGetOneOnlineUserStatus_whenNotEmptyList(List<Long> usersIdList,
//        List<Long> onlineUsersIdList, Map<Long, Boolean> expectedMap) {
//      setUsersStatusesToOnline(onlineUsersIdList);
//
//      Map<Long, Boolean> actualMap = underTest.getAllUsersOnlineStatuses(usersIdList);
//
//      assertEquals(expectedMap, actualMap);
//    }

    private void setUsersStatusesToOnline(List<Long> usersIdList) {
      usersIdList.forEach(OnlineServiceIntegrationTest.this::setUserStatusToOnlineById);
    }

    private static Stream<Arguments> emptyListOrNullArgs() {
      return Stream.of(
          Arguments.of(List.of()),
          Arguments.of((List<Long>) null)
      );
    }

    private static Stream<Arguments> notEmptyListArgs() {
      Long notExistingUserId2 = 888L;
      return Stream.of(
          Arguments.of(List.of(2L, 3L), List.of(), Map.of(2L, false, 3L, false)),
          Arguments.of(List.of(2L, 3L), List.of(2L), Map.of(2L, true, 3L, false)),
          Arguments.of(List.of(2L), List.of(2L), Map.of(2L, true)),
          Arguments.of(List.of(2L, NOT_EXISTING_USER_ID), List.of(), Map.of(2L, false)),
          Arguments.of(List.of(NOT_EXISTING_USER_ID, notExistingUserId2), List.of(2L, 3L), Map.of())
      );
    }

  }

  @Nested
  class GetUserOnlineStatusById {

    @Test
    void shouldThrowNotFound_whenUserNotExist() {
      assertThrows(UserNotFoundException.class,
          () -> underTest.getUserOnlineStatusById(NOT_EXISTING_USER_ID));
    }

  }
  private String createRedisKeyByUserId(Long userId) {
    return REDIS_KEY_TEMPLATE.formatted(userId);
  }

  private void setUserStatusToOnlineById(Long userId) {
    redisTemplate.opsForValue().set(createRedisKeyByUserId(userId), "true");
  }
}
