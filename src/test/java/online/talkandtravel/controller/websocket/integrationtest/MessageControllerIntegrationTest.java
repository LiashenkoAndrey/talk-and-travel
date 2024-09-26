package online.talkandtravel.controller.websocket.integrationtest;

import static online.talkandtravel.testdata.ChatTestData.ANGOLA_CHAT_ID;
import static online.talkandtravel.testdata.ChatTestData.ARUBA_CHAT_ID;
import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.testdata.UserTestData.getAliceSaved;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.config.StompIntegrationTest;
import online.talkandtravel.model.dto.event.EventRequest;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.message.SendMessageRequest;
import online.talkandtravel.model.entity.MessageType;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.EventService;
import online.talkandtravel.util.TestAuthenticationService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.stomp.StompSession;

@Log4j2
public class MessageControllerIntegrationTest extends StompIntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private EventService eventService;

  @Autowired
  private TestAuthenticationService testAuthenticationService;

  private StompSession aliceStompSession;

  private final List<MessageDto> messageDtoList = new ArrayList<>();

  @BeforeAll
  void setUp() throws InterruptedException, ExecutionException {
    saveEntities();
    aliceStompSession = authenticateAndInitializeStompSession(getAlice());
    subscribeToMessages();
  }

  private void saveEntities() {
    userRepository.save(getAliceSaved());
    Principal principal = testAuthenticationService.authenticateUser(getAlice());
    eventService.joinChat(new EventRequest(ARUBA_CHAT_ID), principal);
    eventService.joinChat(new EventRequest(ANGOLA_CHAT_ID), principal);
  }

  @ParameterizedTest
  @MethodSource("sendMessageTestArgs")
  void sendMessageTest(SendMessageRequest request, int index, MessageType messageType, String content, Long chatId) throws Exception {
    aliceStompSession.send(SEND_MESSAGE_PATH, toWSPayload(request));
    Thread.sleep(AFTER_SEND_SLEEP_TIME);

    assertMessageReceived(index, messageType, content, chatId);
  }

  private void assertMessageReceived(int index, MessageType messageType, String content, Long chatId) {
    assertThat(messageDtoList).hasSize(index + 1);
    MessageDto messageDto = messageDtoList.get(index);
    assertEquals(messageType, messageDto.type());
    assertEquals(content, messageDto.content());
    assertEquals(chatId, messageDto.chatId());
  }

  private static Stream<Arguments> sendMessageTestArgs() {
    return Stream.of(
        Arguments.of(new SendMessageRequest("hello!", ARUBA_CHAT_ID, null), 0, MessageType.TEXT, "hello!", ARUBA_CHAT_ID),
        Arguments.of(new SendMessageRequest("how are you??", ARUBA_CHAT_ID, null), 1, MessageType.TEXT, "how are you??", ARUBA_CHAT_ID),
        Arguments.of(new SendMessageRequest("new message1", ANGOLA_CHAT_ID, null), 2, MessageType.TEXT, "new message1", ANGOLA_CHAT_ID),
        Arguments.of(new SendMessageRequest("new message2", ANGOLA_CHAT_ID, null), 3, MessageType.TEXT, "new message2", ANGOLA_CHAT_ID),
        Arguments.of(new SendMessageRequest("new message3", ANGOLA_CHAT_ID, null), 4, MessageType.TEXT, "new message3", ANGOLA_CHAT_ID)
    );
  }

  private void subscribeToMessages() throws InterruptedException {
    subscribe(messageDtoList::add, MessageDto.class, aliceStompSession,
        MESSAGES_SUBSCRIBE_PATH.formatted(ARUBA_CHAT_ID),
        MESSAGES_SUBSCRIBE_PATH.formatted(ANGOLA_CHAT_ID));
  }
}