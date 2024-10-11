package online.talkandtravel.service.impl.integrationtest;

import static online.talkandtravel.config.StompTestConstants.AFTER_SEND_PAUSE_TIME;
import static online.talkandtravel.testdata.UserTestData.ALICE_ID;
import static online.talkandtravel.testdata.UserTestData.LAST_SEEN_ON_REDIS_KEY_PATTERN;
import static online.talkandtravel.testdata.UserTestData.ONLINE_STATUS_REDIS_KEY_PATTERN;
import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.testdata.UserTestData.getAliceSaved;
import static online.talkandtravel.testdata.UserTestData.getBob;
import static online.talkandtravel.testdata.UserTestData.getBobSaved;
import static online.talkandtravel.util.constants.ApiPathConstants.UPDATE_ONLINE_STATUS_PATH;
import static online.talkandtravel.util.constants.ApiPathConstants.USERS_ONLINE_STATUS_ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.config.StompIntegrationTest;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.util.TestAuthenticationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.stomp.StompSession;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Log4j2
public class OnlineServiceWebsocketIntegrationTest extends StompIntegrationTest {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Autowired
  private TestAuthenticationService testAuthenticationService;

  @Autowired
  private UserRepository userRepository;

  private User alise;
  private String aliseToken;
  private StompSession aliseSession;

  private final List<OnlineStatusDto> onlineStatusDtoList = new ArrayList<>();

  @BeforeAll
  void init() throws ExecutionException, InterruptedException {
    setupTestUsers();
    StompSession bobSubscriberSession = authenticateAndSubscribe(getBob());
    aliseSession = authenticateAndSubscribe(getAlice());
    pause(2000);
    subscribeToOnlineStatus(bobSubscriberSession);
  }

  private void setupTestUsers() {
    userRepository.save(getAliceSaved());
    userRepository.save(getBobSaved());
    alise = getAlice();
  }

  private StompSession authenticateAndSubscribe(User user) throws ExecutionException, InterruptedException {
    return authenticateAndInitializeStompSession(user);
  }

  private void subscribeToOnlineStatus(StompSession session) {
    subscribe(onlineStatusDtoList::add, OnlineStatusDto.class, session, USERS_ONLINE_STATUS_ENDPOINT);
  }

  @Test
  @Order(1)
  void shouldSendOnlineStatus_whenAliseLogins() {
    aliseToken = testAuthenticationService.loginAndGetToken(alise.getUserEmail(), alise.getPassword());
    pause(AFTER_SEND_PAUSE_TIME);
    assertOnlineStatusReceived(1);
    verifyAliseOnline(onlineStatusDtoList.get(0));
  }

  @Test
  @Order(2)
  void shouldSendOnlineStatus_whenAliseLogout() {
    testAuthenticationService.logout(aliseToken);
    pause(AFTER_SEND_PAUSE_TIME);
    assertOnlineStatusReceived(2);
    verifyAliseOffline(onlineStatusDtoList.get(1));
  }

  @Test
  @Order(3)
  void shouldSendOnlineStatus_whenAliseStatusUpdatedToOnline() {
    sendStatusUpdate(true);
    assertOnlineStatusReceived(3);
    verifyAliseOnline(onlineStatusDtoList.get(2));
  }

  @Test
  @Order(3)
  void shouldSendOnlineStatus_whenAliseStatusUpdatedToOffline() {
    sendStatusUpdate(false);
    assertOnlineStatusReceived(4);
    verifyStatusUpdateTime();
  }

  @Test
  @Order(4)
  void shouldSendOnlineStatus_whenNewUserRegisters() {
    registerNewUser();
    assertOnlineStatusReceived(5);
    verifyNewUserOnlineStatus(onlineStatusDtoList.get(4));
  }

  @Test
  @Order(5)
  void shouldSendOnlineStatus_whenNewUserInactive() {
    pause(2000);
    assertOnlineStatusReceived(6);
    verifyNewUserOfflineStatus(onlineStatusDtoList.get(5));
  }

  private void sendStatusUpdate(boolean isOnline) {
    aliseSession.send(UPDATE_ONLINE_STATUS_PATH, toWSPayload(isOnline));
    pause(AFTER_SEND_PAUSE_TIME);
  }

  private void registerNewUser() {
    testAuthenticationService.register(new RegisterRequest("jane", "jane@i.ua", "!123456Bb"));
    pause(AFTER_SEND_PAUSE_TIME);
  }


  private void assertOnlineStatusReceived(int expectedSize) {
    assertThat(onlineStatusDtoList).hasSize(expectedSize);
  }

  private void verifyStatusUpdateTime() {
    ZonedDateTime actualLastSeenOn = onlineStatusDtoList.get(3).lastSeenOn();
    ZonedDateTime previousLastSeenOn = onlineStatusDtoList.get(1).lastSeenOn();
    assertTrue(actualLastSeenOn.isAfter(previousLastSeenOn));
  }

  private void verifyAliseOnline(OnlineStatusDto result) {
    verifyOnlineStatus(result, ALICE_ID);
  }

  private void verifyAliseOffline(OnlineStatusDto result) {
    verifyOfflineStatus(result, ALICE_ID);
  }

  private void verifyNewUserOnlineStatus(OnlineStatusDto result) {
    assertTrue(result.isOnline());
    assertNotNull(result.userId());
    assertNull(result.lastSeenOn());

    String onlineStatus = getOnlineStatusFromRedis(result.userId());
    String lastSeenOn = getLastSeenInFromRedis(result.userId());
    assertNotNull(onlineStatus);
    assertTrue(Boolean.parseBoolean(onlineStatus));
    assertNull(lastSeenOn);
  }

  private void verifyNewUserOfflineStatus(OnlineStatusDto result) {
    assertFalse(result.isOnline());
    assertNotNull(result.userId());
    assertNotNull(result.lastSeenOn());

    String onlineStatus = getOnlineStatusFromRedis(result.userId());
    String lastSeenOn = getLastSeenInFromRedis(result.userId());
    assertNull(onlineStatus);
    assertNotNull(lastSeenOn);
  }

  private String getOnlineStatusFromRedis(Long userId) {
    return redisTemplate.opsForValue().get(ONLINE_STATUS_REDIS_KEY_PATTERN.formatted(userId));
  }

  private String getLastSeenInFromRedis(Long userId) {
    return redisTemplate.opsForValue().get(LAST_SEEN_ON_REDIS_KEY_PATTERN.formatted(userId));
  }

  private void verifyOnlineStatus(OnlineStatusDto result, Long userId) {
    assertEquals(userId, result.userId());
    assertTrue(result.isOnline());
    assertNull(result.lastSeenOn());

    String onlineStatus = getOnlineStatusFromRedis(result.userId());
    String lastSeenOn = getLastSeenInFromRedis(result.userId());
    assertNotNull(onlineStatus);
    assertTrue(Boolean.parseBoolean(onlineStatus));
    assertNotNull(lastSeenOn);
  }

  private void verifyOfflineStatus(OnlineStatusDto result, Long userId) {
    assertEquals(userId, result.userId());
    assertFalse(result.isOnline());
    assertNotNull(result.lastSeenOn());

    String onlineStatus = getOnlineStatusFromRedis(result.userId());
    String lastSeenOn = getLastSeenInFromRedis(result.userId());
    assertNull(onlineStatus);
    assertNotNull(lastSeenOn);
  }
}
