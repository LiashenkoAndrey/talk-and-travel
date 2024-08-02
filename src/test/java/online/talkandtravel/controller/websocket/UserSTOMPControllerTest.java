package online.talkandtravel.controller.websocket;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.UserIsTypingDTO;
import online.talkandtravel.util.CustomJSONConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Log4j2
class UserSTOMPControllerTest {

  @LocalServerPort
  private Integer port;

  public WebSocketStompClient webSocketStompClient;

  @BeforeEach
  void setup() {
    this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
        List.of(new WebSocketTransport(new StandardWebSocketClient()))));
    this.webSocketStompClient.setMessageConverter(new CustomJSONConverter());
  }

  @Test
  void verifyGreetingIsReceived() throws Exception {
    long chatId = 1L, userId = 1L;
    String connectUrl = format("ws://localhost:%d/ws", port);
    String subscribePath = format("/countries/%s/texting-users", chatId);
    String sendPath = format("/chat/%s/user/%s/texting-users", chatId, userId);
    String userName = "Andrew";
    UserIsTypingDTO startTypingDTO = new UserIsTypingDTO(userName, true);
    UserIsTypingDTO stopTypingDTO = new UserIsTypingDTO(userName, false);

    BlockingQueue<UserIsTypingDTO> blockingQueue = new ArrayBlockingQueue<>(1);
    CustomStompFrameHandler stompFrameHandler = new CustomStompFrameHandler(blockingQueue);

    StompSession session = webSocketStompClient
        .connectAsync(connectUrl, new StompSessionHandlerAdapter() {})
        .get(1, SECONDS);

    session.subscribe(subscribePath, stompFrameHandler);

    session.send(sendPath, startTypingDTO);
    UserIsTypingDTO expected1 = new UserIsTypingDTO(chatId, userId, userName, true);
    awaitUtilAsserted(expected1, blockingQueue);

    session.send(sendPath, stopTypingDTO);
    UserIsTypingDTO expected2 = new UserIsTypingDTO(chatId, userId, userName, false);
    awaitUtilAsserted(expected2, blockingQueue);
  }

  private void awaitUtilAsserted(UserIsTypingDTO dto, BlockingQueue<UserIsTypingDTO> blockingQueue ) {
    await()
        .atMost(1, SECONDS)
        .untilAsserted(() -> assertEquals(dto, blockingQueue.poll()));
  }

  static class CustomStompFrameHandler implements StompFrameHandler {
    public CustomStompFrameHandler(BlockingQueue<UserIsTypingDTO> blockingQueue) {
      this.blockingQueue = blockingQueue;
    }

    BlockingQueue<UserIsTypingDTO> blockingQueue;
    @Override
    public Type getPayloadType(StompHeaders headers) {
      return UserIsTypingDTO.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
      log.info("handleFrame {}", payload);
      blockingQueue.add((UserIsTypingDTO) payload);
    }
  }
}