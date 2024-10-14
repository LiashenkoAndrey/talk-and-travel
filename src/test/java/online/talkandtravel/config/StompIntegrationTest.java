package online.talkandtravel.config;

import static online.talkandtravel.config.StompTestConstants.AFTER_SUBSCRIBE_SLEEP_TIME;
import static online.talkandtravel.config.StompTestConstants.ONE_SECOND_PAUSE;
import static online.talkandtravel.util.TestAuthenticationService.AUTHORIZATION_HEADER;
import static online.talkandtravel.util.constants.ApiPathConstants.HANDSHAKE_URI;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.TalkAndTravelApplication;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.util.CustomStompSessionHandler;
import online.talkandtravel.util.StompMessageHandler;
import online.talkandtravel.util.TestAuthenticationService;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

/**
 * Integration test base class for handling WebSocket STOMP communication. Provides utility methods
 * for authenticating, initializing STOMP sessions, subscribing to message channels, and sending
 * messages via WebSockets. This class is intended to be extended by other integration test
 * classes.
 *
 * @see WebSocketStompClient
 * @see SockJsClient
 * @see StompSession
 */
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

  @Autowired
  private TestAuthenticationService testAuthenticationService;

  @Autowired
  protected CustomStompSessionHandler customStompSessionHandler;

  private ObjectMapper objectMapper;

  @PostConstruct
  void init() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
  }

  /**
   * Authenticates a user and initializes a STOMP session.
   * Connects to the WebSocket using the provided user's credentials and returns the active session.
   *
   * @param user the user for which the session should be created
   * @return the established {@link StompSession}
   * @throws InterruptedException if the thread is interrupted during connection
   * @throws ExecutionException if there is an error during the connection process
   */
  protected StompSession authenticateAndInitializeStompSession(User user)
      throws InterruptedException, ExecutionException {
    WebSocketStompClient stompClient = createStompSession();
    String token = testAuthenticationService.loginAndGetToken(user.getUserEmail(),
        user.getPassword());

    String handshakeUri = HANDSHAKE_URI.formatted(port);
    WebSocketHttpHeaders headers = createHandshakeHeaders(token);

    StompHeaders stompHeaders = new StompHeaders();
    stompHeaders.add("Authorization", "Bearer " + token);

    StompSession stompSession = stompClient.connectAsync(
        handshakeUri,
        headers,
        stompHeaders,
        customStompSessionHandler
    ).get();

    pause(ONE_SECOND_PAUSE);
    return stompSession;
  }

  private WebSocketStompClient createStompSession() {
    return new WebSocketStompClient(new SockJsClient(
        Arrays.asList(
            new WebSocketTransport(new StandardWebSocketClient()),
            new RestTemplateXhrTransport()
        )
    ));
  }

  private WebSocketHttpHeaders createHandshakeHeaders(String token) {
    WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
    handshakeHeaders.add(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER.formatted(token));
    return handshakeHeaders;
  }

  /**
   * Converts an object to a WebSocket payload by serializing it into JSON.
   *
   * @param <T> the type of the object to serialize
   * @param value the object to serialize
   * @return the JSON representation of the object as a byte array
   * @throws RuntimeException if there is an error during JSON processing
   */
  protected <T> byte[] toWSPayload(T value) {
    try {
      String jsonRequest = objectMapper.writeValueAsString(value);

      return jsonRequest.getBytes(StandardCharsets.UTF_8);
    } catch (JsonProcessingException e) {
      log.error("Json parsing exception: {}", e.getMessage());
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
   * @param endpoints    The endpoints to which the client subscribes.
   * @param messageType  The class type of the messages being received.
   * @param stompSession The STOMP session used for subscribing to the channels.
   * @param consumer     A {@code Consumer} that handles the incoming messages.
   */
  protected <T> void subscribe(Consumer<T> consumer, Class<T> messageType,
      StompSession stompSession, String... endpoints) {

    Stream.of(endpoints).forEach(
        (endpoint) -> subscribe(endpoint, messageType, stompSession, consumer));
    pause(AFTER_SUBSCRIBE_SLEEP_TIME);
  }

  private <T> void subscribe(String endpoint, Class<T> messageType, StompSession stompSession,
      Consumer<T> consumer) {
    stompSession.subscribe(endpoint, new StompMessageHandler<>(consumer, messageType));
  }

  /**
   * Pauses the execution for a specified amount of time.
   */
  protected void pause(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException e) {
      log.error("An exception occupied when sleep: {}", e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
