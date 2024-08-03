package online.talkandtravel.controller.websocket;

import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.UserIsTypingDTORequest;
import online.talkandtravel.model.dto.UserIsTypingDTOResponse;
import online.talkandtravel.util.CustomJSONConverter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
@TestInstance(Lifecycle.PER_CLASS)
class UserSTOMPControllerTest {

  @LocalServerPort
  private Integer port;
  long chatId = 1L, userId = 1L;
  String userName = "Andrew";
  String sendPath = format("/chat/%s/user/%s/texting-users", chatId, userId);
  BlockingQueue<UserIsTypingDTOResponse> blockingQueue = new ArrayBlockingQueue<>(1);

  public WebSocketStompClient webSocketStompClient;
  public StompSession session;
  @BeforeAll
  void setup() throws ExecutionException, InterruptedException, TimeoutException {
    this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
        List.of(new WebSocketTransport(new StandardWebSocketClient()))));
    this.webSocketStompClient.setMessageConverter(new CustomJSONConverter());
    this.session = webSocketStompClient
        .connectAsync(getConnectionUrl(port), new StompSessionHandlerAdapter() {})
        .get(1, SECONDS);
  }

  @Test
  void onUserStartOrStopTyping_shouldNotifyAllSubscribedUsers() {
    String subscribePath = format("/countries/%s/texting-users", chatId);
    UserIsTypingDTORequest startTypingDTO = new UserIsTypingDTORequest(userName, true);
    UserIsTypingDTORequest stopTypingDTO = new UserIsTypingDTORequest(userName, false);
    CustomStompFrameHandler stompFrameHandler = new CustomStompFrameHandler(blockingQueue);

    session.subscribe(subscribePath, stompFrameHandler);

    session.send(sendPath, startTypingDTO);
    var expected1 = new UserIsTypingDTOResponse(chatId, userId, userName, true);
    awaitUtilAsserted(expected1, blockingQueue);

    session.send(sendPath, stopTypingDTO);
    var expected2 = new UserIsTypingDTOResponse(chatId, userId, userName, false);
    awaitUtilAsserted(expected2, blockingQueue);
  }

  private String getConnectionUrl(Integer port) {
    return format("ws://localhost:%d/ws", port);
  }

  private void awaitUtilAsserted(UserIsTypingDTOResponse dto, BlockingQueue<UserIsTypingDTOResponse> blockingQueue ) {
    await()
        .atMost(1, SECONDS)
        .untilAsserted(() -> assertEquals(dto, blockingQueue.poll()));
  }

  static class CustomStompFrameHandler implements StompFrameHandler {
    public CustomStompFrameHandler(BlockingQueue<UserIsTypingDTOResponse> blockingQueue) {
      this.blockingQueue = blockingQueue;
    }

    BlockingQueue<UserIsTypingDTOResponse> blockingQueue;
    @Override
    public Type getPayloadType(StompHeaders headers) {
      return UserIsTypingDTOResponse.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
      log.info("handleFrame {}", payload);
      blockingQueue.add((UserIsTypingDTOResponse) payload);
    }
  }
}