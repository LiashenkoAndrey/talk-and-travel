package online.talkandtravel.controller.websocket.integrationtest;

import static online.talkandtravel.config.TestDataConstant.USERS_DATA_SQL;
import static online.talkandtravel.testdata.ChatTestData.ANGOLA_CHAT_ID;
import static online.talkandtravel.testdata.ChatTestData.ARUBA_CHAT_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.test.context.jdbc.Sql;

@Log4j2
@Sql({USERS_DATA_SQL})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MessageControllerIntegrationTest extends StompIntegrationTest {

  private final List<MessageDto> messageDtoList = new ArrayList<>();

  @BeforeAll
  void setUp() throws InterruptedException, ExecutionException {
    configureRestTemplate();
    initializeStompSession();
    subscribeToMessages();
  }

  @Order(1)
  @ParameterizedTest
  @MethodSource("subscribeToMessagesTestArgs")
  void subscribeToMessagesTest(EventRequest request, int index, MessageType messageType, String content, Long chatId) throws Exception {
    stompSession.send(JOIN_CHAT_EVENT_PATH, toWSPayload(request));
    Thread.sleep(AFTER_SEND_SLEEP_TIME);

    assertMessageReceived(index, messageType, content, chatId);
  }

  @Order(2)
  @ParameterizedTest
  @MethodSource("sendMessageTestArgs")
  void sendMessageTest(SendMessageRequest request, int index, MessageType messageType, String content, Long chatId) throws Exception {
    stompSession.send(SEND_MESSAGE_PATH, toWSPayload(request));
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

  private static Stream<Arguments> subscribeToMessagesTestArgs() {
    return Stream.of(
        Arguments.of(new EventRequest(ARUBA_CHAT_ID), 0, MessageType.JOIN, "admin joined the chat", ARUBA_CHAT_ID),
        Arguments.of(new EventRequest(ANGOLA_CHAT_ID), 1, MessageType.JOIN, "admin joined the chat", ANGOLA_CHAT_ID)
    );
  }

  private static Stream<Arguments> sendMessageTestArgs() {
    return Stream.of(
        Arguments.of(new SendMessageRequest("hello!", ARUBA_CHAT_ID, null), 2, MessageType.TEXT, "hello!", ARUBA_CHAT_ID),
        Arguments.of(new SendMessageRequest("how are you??", ARUBA_CHAT_ID, null), 3, MessageType.TEXT, "how are you??", ARUBA_CHAT_ID),
        Arguments.of(new SendMessageRequest("new message1", ANGOLA_CHAT_ID, null), 4, MessageType.TEXT, "new message1", ANGOLA_CHAT_ID),
        Arguments.of(new SendMessageRequest("new message2", ANGOLA_CHAT_ID, null), 5, MessageType.TEXT, "new message2", ANGOLA_CHAT_ID),
        Arguments.of(new SendMessageRequest("new message3", ANGOLA_CHAT_ID, null), 6, MessageType.TEXT, "new message3", ANGOLA_CHAT_ID)
    );
  }

  private void subscribeToMessages() throws InterruptedException {
    stompSession.subscribe(MESSAGES_SUBSCRIBE_PATH.formatted(ARUBA_CHAT_ID), new CustomStompFrameHandler());
    stompSession.subscribe(MESSAGES_SUBSCRIBE_PATH.formatted(ANGOLA_CHAT_ID), new CustomStompFrameHandler());

    Thread.sleep(AFTER_SUBSCRIBE_SLEEP_TIME);
  }

  private class CustomStompFrameHandler implements StompFrameHandler {
    @Override
    public Type getPayloadType(StompHeaders headers) {
      return Object.class;
    }
    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
      try {
        MessageDto message = parseMessageDto(payload);
        messageDtoList.add(message);
        log.info("Received message: {}, headers: {}", message, headers);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    private MessageDto parseMessageDto(Object payload) throws JsonProcessingException {
      String jsonString = new String((byte[]) payload, StandardCharsets.UTF_8);
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
      return objectMapper.readValue(jsonString, MessageDto.class);
    }
  }
}