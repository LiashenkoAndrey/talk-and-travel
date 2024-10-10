package online.talkandtravel.service.impl.integrationtest;

import static online.talkandtravel.config.TestDataConstant.PRIVATE_CHATS_DATA_SQL;
import static online.talkandtravel.config.TestDataConstant.USERS_DATA_SQL;
import static online.talkandtravel.testdata.UserTestData.ALICE_ID;
import static online.talkandtravel.testdata.UserTestData.ALISE_LAST_SEEN_ON_REDIS_KEY;
import static online.talkandtravel.testdata.UserTestData.ALISE_ONLINE_STATUS_REDIS_KEY;
import static online.talkandtravel.testdata.UserTestData.BOB_ID;
import static online.talkandtravel.testdata.UserTestData.BOB_LAST_SEEN_ON_REDIS_KEY;
import static online.talkandtravel.testdata.UserTestData.BOB_ONLINE_STATUS_REDIS_KEY;
import static online.talkandtravel.testdata.UserTestData.TOMAS_ID;
import static online.talkandtravel.testdata.UserTestData.TOMAS_LAST_SEEN_ON_REDIS_KEY;
import static online.talkandtravel.testdata.UserTestData.TOMAS_ONLINE_STATUS_REDIS_KEY;
import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.testdata.UserTestData.getBob;
import static online.talkandtravel.testdata.UserTestData.getTomas;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.config.IntegrationTest;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.model.dto.user.OnlineStatusResponse;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.service.OnlineService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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

/**
 * Alise is offline, last seen on - 2 days from now
 * Tomas is online, last seen on - now
 * Bob is offline, last seen on - null
 */
@Log4j2
@Sql({USERS_DATA_SQL, PRIVATE_CHATS_DATA_SQL})
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
  private static User alice, bob, tomas;
  private static final LocalDateTime aliseLastSeenOn = LocalDateTime.now().minusDays(2);
  private static final LocalDateTime tomasLastSeenOn = LocalDateTime.now();

  @BeforeEach
  void init() {
    alice = getAlice();
    bob = getBob();
    tomas = getTomas();
    redisTemplate.opsForValue().set(ALISE_LAST_SEEN_ON_REDIS_KEY, aliseLastSeenOn.toString());
    redisTemplate.opsForValue().set(TOMAS_LAST_SEEN_ON_REDIS_KEY, tomasLastSeenOn.toString());
    redisTemplate.opsForValue().set(TOMAS_ONLINE_STATUS_REDIS_KEY, Boolean.TRUE.toString());
  }

  @AfterEach
  void clearRedis() {
    try (RedisConnection connection = RedisConnectionUtils.getConnection(redisConnectionFactory)) {
      connection.serverCommands().flushDb();
    }
  }

  @Nested
  class UpdateUserOnlineStatus {

    @Test
    void shouldUpdateAliseStatusToOnlineAndVerifySetKeyExpirationTime() {

      OnlineStatusDto result = underTest.updateUserOnlineStatus(ALICE_ID, true);

      assertEquals(true, result.isOnline());
      assertEquals(ALICE_ID, result.userId());
      assertEquals(true, redisTemplate.expire(ALISE_ONLINE_STATUS_REDIS_KEY, Duration.ofSeconds(redisKeyExpirationInSec)));

      String onlineStatusInRedis = redisTemplate.opsForValue().get(ALISE_ONLINE_STATUS_REDIS_KEY);
      assertNotNull(onlineStatusInRedis, "Online status should be stored in Redis");
      assertEquals("true", onlineStatusInRedis, "Online status in Redis should be 'true'");
    }

    @Test
    void shouldUpdateTomasStatusToOffline() {
      OnlineStatusDto result = underTest.updateUserOnlineStatus(TOMAS_ID, false);

      assertEquals(false, result.isOnline());
      assertEquals(TOMAS_ID, result.userId());
      assertTrue(result.lastSeenOn().isAfter(tomasLastSeenOn));
      assertEquals(false, redisTemplate.hasKey(TOMAS_ONLINE_STATUS_REDIS_KEY));
      assertEquals(true, redisTemplate.hasKey(TOMAS_LAST_SEEN_ON_REDIS_KEY));
    }

    @Test
    void shouldUpdateBobStatusToOnline() {
      assertEquals(false, redisTemplate.hasKey(BOB_LAST_SEEN_ON_REDIS_KEY));
      assertEquals(false, redisTemplate.hasKey(BOB_ONLINE_STATUS_REDIS_KEY));

      OnlineStatusDto result = underTest.updateUserOnlineStatus(BOB_ID, true);

      assertEquals(true, result.isOnline());
      assertNull(result.lastSeenOn());
      assertEquals(BOB_ID, result.userId());
      assertEquals(true, redisTemplate.hasKey(BOB_ONLINE_STATUS_REDIS_KEY));
      assertEquals(false, redisTemplate.hasKey(BOB_LAST_SEEN_ON_REDIS_KEY));
    }

    @Test
    void shouldUpdateBobStatusToOffline() {
      OnlineStatusDto result = underTest.updateUserOnlineStatus(BOB_ID, false);

      assertEquals(false, result.isOnline());
      assertNotNull(result.lastSeenOn());
      assertEquals(BOB_ID, result.userId());
      assertEquals(true, redisTemplate.hasKey(BOB_LAST_SEEN_ON_REDIS_KEY));
    }
  }

  @Nested
  class GetAllUsersOnlineStatuses {

    @ParameterizedTest
    @MethodSource("emptyListOrNullArgs")
    void shouldGetAllOfflineUsers_whenEmptyListOrNull(List<Long> usersIdList ) {
      Map<Long, OnlineStatusResponse> actualMap = underTest.getAllUsersOnlineStatuses(usersIdList);

      assertThat(actualMap).hasSize(4);

      assertThat(actualMap).hasEntrySatisfying(alice.getId(), (statusResponse) -> {
        assertFalse(statusResponse.isOnline());
        assertEquals(statusResponse.lastSeenOn(), aliseLastSeenOn);
      });
      assertThat(actualMap).hasEntrySatisfying(bob.getId(), (statusResponse) -> {
        assertFalse(statusResponse.isOnline());
        assertNull(statusResponse.lastSeenOn());
      });
      assertThat(actualMap).hasEntrySatisfying(tomas.getId(), (statusResponse) -> {
        assertTrue(statusResponse.isOnline());
        assertEquals(tomasLastSeenOn, statusResponse.lastSeenOn());
      });
    }

    @ParameterizedTest
    @MethodSource("emptyListOrNullArgs")
    void shouldGetOnlineUsers_whenEmptyListOrNullAndHasAliseOnline(List<Long> usersIdList ) {
      setUserStatusToOnlineById(alice.getId());

      Map<Long, OnlineStatusResponse> actualMap = underTest.getAllUsersOnlineStatuses(usersIdList);
      assertThat(actualMap).hasSize(4);

      assertThat(actualMap).hasEntrySatisfying(alice.getId(), (statusResponse) -> {
        assertTrue(statusResponse.isOnline());
        assertEquals(statusResponse.lastSeenOn(), aliseLastSeenOn);
      });
      assertThat(actualMap).hasEntrySatisfying(bob.getId(), (statusResponse) -> {
        assertFalse(statusResponse.isOnline());
        assertNull(statusResponse.lastSeenOn());
      });
      assertThat(actualMap).hasEntrySatisfying(tomas.getId(), (statusResponse) -> {
        assertTrue(statusResponse.isOnline());
        assertEquals(tomasLastSeenOn, statusResponse.lastSeenOn());
      });
    }

    @ParameterizedTest
    @MethodSource("notEmptyListArgs")
    void shouldGetOneOnlineUserStatus_whenNotEmptyList(List<Long> usersIdList,
        List<Long> onlineUsersIdList, Map<Long, OnlineStatusResponse> expectedMap) {

      setUsersStatusesToOnline(onlineUsersIdList);

      Map<Long, OnlineStatusResponse> actualMap = underTest.getAllUsersOnlineStatuses(usersIdList);

      assertEquals(expectedMap, actualMap);
    }

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
          Arguments.of(List.of(ALICE_ID, BOB_ID), List.of(), Map.of(ALICE_ID, aliseResp(false), BOB_ID, bobResp(false))),
          Arguments.of(List.of(ALICE_ID, BOB_ID), List.of(ALICE_ID), Map.of(ALICE_ID, aliseResp(true), BOB_ID, bobResp(false))),
          Arguments.of(List.of(ALICE_ID), List.of(ALICE_ID), Map.of(ALICE_ID, aliseResp(true))),
          Arguments.of(List.of(ALICE_ID, NOT_EXISTING_USER_ID), List.of(), Map.of(ALICE_ID, aliseResp(false))),
          Arguments.of(List.of(NOT_EXISTING_USER_ID, notExistingUserId2), List.of(ALICE_ID, BOB_ID), Map.of()),
          Arguments.of(List.of(TOMAS_ID), List.of(), Map.of(TOMAS_ID, new OnlineStatusResponse(true, tomasLastSeenOn)))
      );
    }

    private static OnlineStatusResponse aliseResp(boolean isOnline) {
      return  new OnlineStatusResponse(isOnline, aliseLastSeenOn);
    }

    private static OnlineStatusResponse bobResp(boolean isOnline) {
      return new OnlineStatusResponse(isOnline, null);
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
    redisTemplate.opsForValue().set(createRedisKeyByUserId(userId), Boolean.TRUE.toString());
  }
}
