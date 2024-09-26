package online.talkandtravel.controller.websocket.integrationtest;

import static online.talkandtravel.testdata.ChatTestData.ANGOLA_CHAT_ID;
import static online.talkandtravel.testdata.ChatTestData.ARUBA_CHAT_ID;
import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.testdata.UserTestData.getAliceSaved;
import static online.talkandtravel.testdata.UserTestData.getBob;
import static online.talkandtravel.testdata.UserTestData.getBobSaved;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.config.StompIntegrationTest;
import online.talkandtravel.model.dto.event.EventRequest;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.model.entity.MessageType;
import online.talkandtravel.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;

@Log4j2
public class EventControllerIntegrationTest extends StompIntegrationTest {

  @Autowired private UserRepository userRepository;

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class OnlineStatusTests {
    private final List<OnlineStatusDto> onlineStatusDtoList = new ArrayList<>();

    private StompSession aliceStompSession;
    private StompSession bobStompSession;

    @BeforeAll
    void setUp() throws InterruptedException, ExecutionException {
      userRepository.save(getAliceSaved());
      userRepository.save(getBobSaved());
      aliceStompSession = authenticateAndInitializeStompSession(getAlice());
      bobStompSession = authenticateAndInitializeStompSession(getBob());

      subscribeToOnlineStatus();
    }

    @Order(1)
    @ParameterizedTest
    @MethodSource("chatEventsTestArgs")
    void chatEventsTest(Integer index, StompSession stompSession, Long userId, Boolean isOnline) throws Exception {
      stompSession.send(UPDATE_ONLINE_STATUS_PATH, toWSPayload(isOnline));
      Thread.sleep(AFTER_SEND_SLEEP_TIME);
      assertMessageReceived(index, userId, isOnline);
    }

    private Stream<Arguments> chatEventsTestArgs() {
      return Stream.of(
          Arguments.of(1, aliceStompSession, 2L, true),
          Arguments.of(2, bobStompSession, 3L, true),
          Arguments.of(3, aliceStompSession, 2L, false),
          Arguments.of(4, aliceStompSession,2L, true)
      );
    }

    @Order(2)
    @ParameterizedTest
    @MethodSource("verifyOnlineStatusIsOfflineArgs")
    void verifyOnlineStatusIsOffline(Integer index, Long userId, Boolean isOnline) throws Exception {
      Thread.sleep(1000);
      assertMessageReceived(index, userId, isOnline);
    }

    private Stream<Arguments> verifyOnlineStatusIsOfflineArgs() {
      return Stream.of(
          Arguments.of(5, 3L, false),
          Arguments.of(6, 2L, false)
      );
    }

    private void assertMessageReceived(Integer index, Long userId, Boolean isOnline) {
      OnlineStatusDto onlineStatusDto = onlineStatusDtoList.get(index);
      assertEquals(userId, onlineStatusDto.userId());
      assertEquals(isOnline, onlineStatusDto.isOnline());
    }

    private void subscribeToOnlineStatus() throws InterruptedException {
      subscribe(onlineStatusDtoList::add, OnlineStatusDto.class, aliceStompSession,
          USERS_ONLINE_STATUS_ENDPOINT);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class ChatEventsTests {

    private StompSession aliceStompSession;
    private StompSession bobStompSession;

    private final List<MessageDto> messageDtoList = new ArrayList<>();

    @BeforeAll
    void setUp() throws InterruptedException, ExecutionException {
      saveEntities();
      aliceStompSession = authenticateAndInitializeStompSession(getAlice());
      bobStompSession = authenticateAndInitializeStompSession(getBob());

      subscribeToChatEvents();
    }

    @ParameterizedTest
    @MethodSource("chatEventsTestArgs")
    void chatEventsTest(Integer index, StompSession stompSession, String path, Long chatId,
        MessageType messageType, String content) throws Exception {
      stompSession.send(path, toWSPayload(new EventRequest(chatId)));
      Thread.sleep(AFTER_SEND_SLEEP_TIME);
      if (index != null) {
        assertMessageReceived(index, messageType, content, chatId);
      }
    }

    private void assertMessageReceived(Integer index, MessageType messageType, String content, Long chatId) {
      assertThat(messageDtoList).hasSize(index + 1);
      MessageDto messageDto = messageDtoList.get(index);
      assertEquals(messageType, messageDto.type());
      assertEquals(content, messageDto.content());

      if (messageDto.chatId() != null) {
        assertEquals(chatId, messageDto.chatId());
      }
    }

    private Stream<Arguments> chatEventsTestArgs() {
      return Stream.of(
          Arguments.of(0, aliceStompSession, JOIN_CHAT_EVENT_PATH, ARUBA_CHAT_ID, MessageType.JOIN, "Alice joined the chat"),
          Arguments.of(null, aliceStompSession, JOIN_CHAT_EVENT_PATH, ANGOLA_CHAT_ID, null, null),
          Arguments.of(null, bobStompSession, JOIN_CHAT_EVENT_PATH, ANGOLA_CHAT_ID, null, null),
          Arguments.of(null, bobStompSession, START_TYPING_EVENT_PATH, ANGOLA_CHAT_ID, null, null),
          Arguments.of(1, aliceStompSession, START_TYPING_EVENT_PATH, ARUBA_CHAT_ID, MessageType.START_TYPING, null),
          Arguments.of(2, aliceStompSession, STOP_TYPING_EVENT_PATH, ARUBA_CHAT_ID, MessageType.STOP_TYPING, null),
          Arguments.of(3, aliceStompSession, LEAVE_CHAT_EVENT_PATH, ARUBA_CHAT_ID, MessageType.LEAVE, "Alice left the chat"),
          Arguments.of(null, bobStompSession, LEAVE_CHAT_EVENT_PATH, ANGOLA_CHAT_ID, null, null)
      );
    }

    private void saveEntities() {
      userRepository.save(getAliceSaved());
      userRepository.save(getBobSaved());
    }

    private void subscribeToChatEvents() throws InterruptedException {
      subscribe(messageDtoList::add, MessageDto.class, aliceStompSession,
          MESSAGES_SUBSCRIBE_PATH.formatted(ARUBA_CHAT_ID));
    }
  }
}