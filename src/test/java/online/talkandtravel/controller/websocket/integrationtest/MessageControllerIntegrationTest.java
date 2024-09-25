package online.talkandtravel.controller.websocket.integrationtest;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.TalkAndTravelApplication;
import online.talkandtravel.config.IntegrationTest;
import online.talkandtravel.config.TestConfig;
import online.talkandtravel.facade.AuthenticationFacade;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.dto.auth.LoginRequest;
import online.talkandtravel.model.entity.Token;
import online.talkandtravel.model.entity.TokenType;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.TokenRepository;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.util.TestAuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static online.talkandtravel.config.TestDataConstant.USERS_DATA_SQL;
import static online.talkandtravel.testdata.UserTestData.getAlice;

@Log4j2
@Transactional
@Import({TestConfig.class})
@AutoConfigureMockMvc
@SpringBootTest(classes = TalkAndTravelApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({USERS_DATA_SQL})
public class MessageControllerIntegrationTest  {

    @LocalServerPort
    private int port;

    @Autowired
    private TestAuthenticationService testAuthenticationService;

    @Autowired AuthenticationFacade authenticationFacade;
    private WebSocketStompClient stompClient;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    private static final String WEBSOCKET_URI = "http://localhost:%s/ws"; // For WebSocket


    @BeforeEach
    void setUp() throws InterruptedException {
        Thread.sleep(5000);
        log.info("port : {}", port);
        stompClient = new WebSocketStompClient(new SockJsClient(
                Arrays.asList(
                        new WebSocketTransport(new StandardWebSocketClient()),
                        new RestTemplateXhrTransport())
        ));
    }

    @Test
    void sendMessageTest() throws Exception {
        log.info("port : {}", port);
        log.info("handshake - {}", WEBSOCKET_URI.formatted(port));
        User alice = getAlice();
        AuthResponse authResponse = authenticationFacade.login(new LoginRequest(alice.getUserEmail(), alice.getPassword()));
        Token token= tokenRepository.save(Token.builder()
                        .user(userRepository.findById(alice.getId()).orElseThrow(EntityNotFoundException::new))
                        .tokenType(TokenType.BEARER)
                        .expired(false)
                        .revoked(false)
                        .token(authResponse.token())
                .build());
        log.info("token : {}", token);
        List<Token> list = tokenRepository.findAllByUserId(alice.getId());
        log.info("list : {}", list);
        log.info("authResponse : {}", authResponse);
        // Prepare the WebSocketHttpHeaders for the handshake (optional if needed for SockJS or other parts)
        WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
        handshakeHeaders.add("Authorization", "Bearer " + authResponse.token());

        // Prepare the StompHeaders with the Authorization token for STOMP
        StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add("Authorization", "Bearer " + authResponse.token());

        log.info("hanshake headers: {}, stomp headers: {}", handshakeHeaders, stompHeaders);
        StompSession session = stompClient.connectAsync(
                WEBSOCKET_URI.formatted(port),
                handshakeHeaders,
                stompHeaders,
                new CustomStompSessionHandler()
                ).get();
        log.info("session : {}", session);
    }

    @Log4j2
    private static class CustomStompSessionHandler implements StompSessionHandler {

        /**
         * Invoked when the session is ready to use, i.e. after the underlying
         * transport (TCP, WebSocket) is connected and a STOMP CONNECTED frame is
         * received from the broker.
         *
         * @param session          the client STOMP session
         * @param connectedHeaders the STOMP CONNECTED frame headers
         */
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            log.info("Connected, sessionId: {}", session.getSessionId());

        }

        /**
         * Handle any exception arising while processing a STOMP frame such as a
         * failure to convert the payload or an unhandled exception in the
         * application {@code StompFrameHandler}.
         *
         * @param session   the client STOMP session
         * @param command   the STOMP command of the frame
         * @param headers   the headers
         * @param payload   the raw payload
         * @param exception the exception
         */
        @Override
        public void handleException(StompSession session, StompCommand command,
                                    StompHeaders headers, byte[] payload, Throwable exception) {
            log.info("handleException, sessionId: {}, command: {}, payload: {}",
                    session.getSessionId(), command.getMessageType(), payload, exception);
        }

        /**
         * Handle a low level transport error which could be an I/O error or a
         * failure to encode or decode a STOMP message.
         * <p>Note that
         * {@link ConnectionLostException
         * ConnectionLostException} will be passed into this method when the
         * connection is lost rather than closed normally via
         * {@link StompSession#disconnect()}.
         *
         * @param session   the client STOMP session
         * @param exception the exception that occurred
         */
        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            log.info("handleTransportError, sessionId: {}", session.getSessionId(), exception);
        }

        /**
         * Invoked before {@link #handleFrame(StompHeaders, Object)} to determine the
         * type of Object the payload should be converted to.
         *
         * @param headers the headers of a message
         */
        @Override
        public Type getPayloadType(StompHeaders headers) {
            log.info("getPayloadType {}", headers);
            return null;
        }

        /**
         * Handle a STOMP frame with the payload converted to the target type returned
         * from {@link #getPayloadType(StompHeaders)}.
         *
         * @param headers the headers of the frame
         * @param payload the payload, or {@code null} if there was no payload
         */
        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            log.info("handleFrame, {} {}", headers, payload);
        }
    }
}
