package online.talkandtravel.config;

import static online.talkandtravel.testdata.UserTestData.getAdmin;
import static online.talkandtravel.util.TestAuthenticationService.AUTHENTICATION_URL;
import static online.talkandtravel.util.TestAuthenticationService.AUTHORIZATION_HEADER;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import online.talkandtravel.TalkAndTravelApplication;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.dto.auth.LoginRequest;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.util.CustomStompSessionHandler;
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
public class StompIntegrationTest {

  @LocalServerPort
  protected int port;

  @Autowired
  protected TestRestTemplate restTemplate;

  protected StompSession stompSession;

  protected static final long AFTER_SUBSCRIBE_SLEEP_TIME = 1000L, AFTER_SEND_SLEEP_TIME = 400L;

  protected static final String HANDSHAKE_URI = "http://localhost:%s/ws",
      MESSAGES_SUBSCRIBE_PATH = "/countries/%s/messages",
      JOIN_CHAT_EVENT_PATH = "/chat/events.joinChat",
      SEND_MESSAGE_PATH = "/chat/messages";


  protected void initializeStompSession() throws InterruptedException, ExecutionException {
    WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(
        Arrays.asList(
            new WebSocketTransport(new StandardWebSocketClient()),
            new RestTemplateXhrTransport()
        )
    ));
    User admin = getAdmin();
    LoginRequest loginRequest = new LoginRequest(admin.getUserEmail(), admin.getPassword());
    AuthResponse response = restTemplate.postForObject(AUTHENTICATION_URL.formatted(port), loginRequest, AuthResponse.class);

    WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
    handshakeHeaders.add(HttpHeaders.AUTHORIZATION, AUTHORIZATION_HEADER.formatted(response.token()));

    stompSession = stompClient.connectAsync(
        HANDSHAKE_URI.formatted(port),
        handshakeHeaders,
        new CustomStompSessionHandler()
    ).get();
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

  protected void configureRestTemplate() {
    restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    restTemplate.getRestTemplate().setErrorHandler(new DefaultResponseErrorHandler() {
      @Override
      public boolean hasError(ClientHttpResponse response) throws IOException {
        HttpStatusCode statusCode = response.getStatusCode();
        return statusCode.isError();
      }
    });
  }
}
