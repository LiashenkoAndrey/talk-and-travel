package online.talkandtravel.config;

import static online.talkandtravel.util.TestAuthenticationService.AUTHENTICATION_URL;
import static online.talkandtravel.util.TestAuthenticationService.AUTHORIZATION_HEADER;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.TalkAndTravelApplication;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.dto.auth.LoginRequest;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.util.CustomStompSessionHandler;
import online.talkandtravel.util.StompMessageHandler;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

@Transactional
@Import({TestConfig.class})
@AutoConfigureMockMvc
@SpringBootTest(classes = TalkAndTravelApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(Lifecycle.PER_CLASS)
@Log4j2
public class StompIntegrationTest {

  @LocalServerPort
  protected int port;

  @Autowired
  protected TestRestTemplate restTemplate;

  @Autowired
  protected MockMvc mockMvc;

  protected static final long AFTER_SUBSCRIBE_SLEEP_TIME = 1000L, AFTER_SEND_SLEEP_TIME = 400L;

  protected static final String HANDSHAKE_URI = "http://localhost:%s/ws",
      MESSAGES_SUBSCRIBE_PATH = "/countries/%s/messages",
      JOIN_CHAT_EVENT_PATH = "/chat/events.joinChat",
      START_TYPING_EVENT_PATH = "/chat/events.startTyping",
      STOP_TYPING_EVENT_PATH = "/chat/events.stopTyping",
      LEAVE_CHAT_EVENT_PATH = "/chat/events.leaveChat",
      SEND_MESSAGE_PATH = "/chat/messages",
      UPDATE_ONLINE_STATUS_PATH = "/auth-user/events.updateOnlineStatus",
      USERS_ONLINE_STATUS_ENDPOINT = "/users/onlineStatus";

  protected StompSession authenticateAndInitializeStompSession(User user) throws InterruptedException, ExecutionException {
    WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(
        Arrays.asList(
            new WebSocketTransport(new StandardWebSocketClient()),
            new RestTemplateXhrTransport()
        )
    ));
    LoginRequest loginRequest = new LoginRequest(user.getUserEmail(), user.getPassword());
    AuthResponse response = restTemplate.postForObject(AUTHENTICATION_URL.formatted(port), loginRequest, AuthResponse.class);

    WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
    handshakeHeaders.add(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER.formatted(response.token()));

    StompSession stompSession = stompClient.connectAsync(
        HANDSHAKE_URI.formatted(port),
        handshakeHeaders,
        new CustomStompSessionHandler()
    ).get();

    Thread.sleep(1000);
    return stompSession;
  }

  protected <T> byte[] toWSPayload(T value) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
      String jsonRequest = objectMapper.writeValueAsString(value);

      return jsonRequest.getBytes(StandardCharsets.UTF_8);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Subscribes to multiple chat channels and processes incoming messages.
   * <p>
   * The {@code subscribe} method is invoked as follows:
   * <pre>{@code
   *    subscribe((message) -> {
   *           messageDtoList.add(message);
   *           log.info("Received message: {}", message);
   *     }, MessageDto.class,
   *         aliceStompSession,
   *         MESSAGES_SUBSCRIBE_PATH.formatted(ARUBA_CHAT_ID),
   *         MESSAGES_SUBSCRIBE_PATH.formatted(ANGOLA_CHAT_ID));
   * }</pre>
   *
   * @param <T>          The type of message being subscribed to (e.g., {@code MessageDto.class}).
   * @param endpoints     The endpoints to which the client subscribes.
   * @param messageType  The class type of the messages being received.
   * @param stompSession The STOMP session used for subscribing to the channels.
   * @param consumer     A {@code Consumer} that handles the incoming messages.
   */
  protected <T> void subscribe(Consumer<T> consumer, Class<T> messageType, StompSession stompSession, String... endpoints)
      throws InterruptedException {
    for (String endpoint : endpoints) {
      subscribe(endpoint, messageType, stompSession, consumer);
    }
    Thread.sleep(AFTER_SUBSCRIBE_SLEEP_TIME);
  }

  protected <T> void subscribe(String endpoint, Class<T> messageType, StompSession stompSession, Consumer<T> consumer)
      throws InterruptedException {
      stompSession.subscribe(endpoint, new StompMessageHandler<>(consumer, messageType));
    Thread.sleep(AFTER_SUBSCRIBE_SLEEP_TIME);
  }

  @PostConstruct
  protected void configureRestTemplate() {
    restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    restTemplate.getRestTemplate().setErrorHandler(new DefaultResponseErrorHandler() {
      @Override
      public boolean hasError(@NotNull ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        return statusCode.isError();
      }
    });
  }
}
