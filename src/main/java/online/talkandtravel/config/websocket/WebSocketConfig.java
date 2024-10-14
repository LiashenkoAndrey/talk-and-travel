package online.talkandtravel.config.websocket;

import static online.talkandtravel.util.constants.ApiPathConstants.AUTH_USER_APPLICATION_DESTINATION;
import static online.talkandtravel.util.constants.ApiPathConstants.CHATS_BROKER_DESTINATION;
import static online.talkandtravel.util.constants.ApiPathConstants.CHAT_APPLICATION_DESTINATION;
import static online.talkandtravel.util.constants.ApiPathConstants.USER_BROKER_DESTINATION;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket configuration class for setting up STOMP messaging in the application.
 *
 * <p>This configuration enables WebSocket message handling and sets up STOMP (Simple Text Oriented
 * Messaging Protocol) endpoints and message brokers. It allows WebSocket connections from specified
 * origins and configures message destination prefixes and brokers.
 *
 * <p>Key aspects of the configuration include:
 *
 * <ul>
 *   <li><strong>STOMP Endpoints:</strong> Registers the STOMP endpoint "/ws" with support for
 *       SockJS to provide fallback options for clients that do not support WebSockets.
 *   <li><strong>Allowed Origins:</strong> Specifies a list of allowed origins to enable
 *       cross-origin WebSocket connections.
 *   <li><strong>Message Broker:</strong> Configures the message broker:
 *       <ul>
 *         <li><strong>Application Destination Prefixes:</strong> Sets the prefix for application
 *             destinations (i.e., destinations that are targeted by clients) to "/chat".
 *         <li><strong>Enabled Simple Broker:</strong> Enables a simple in-memory message broker
 *             with destinations "/countries" and "/group-messages" for broadcasting messages to
 *             subscribed clients.
 *       </ul>
 * </ul>
 *
 */
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Log4j2
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final OnConnectChannelInterceptor onConnectChannelInterceptor;

  private final String[] ALLOWED_URL =
      new String[] {
        "http://localhost:3001",
        "http://localhost:3000",
        "http://localhost:5500",
        "https://oleksandrprokopenkodev.github.io",
        "https://splendorous-kringle-40d196.netlify.app",
        "http://localhost:63342",
        "http://localhost:8080",
        "https://reginavarybrus.github.io"
      };

  @Override
  public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors(onConnectChannelInterceptor);
  }

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns(ALLOWED_URL)
        .withSockJS();
  }

  /**
   * Configures the message broker for handling STOMP messages in the application.
   *
   * <p>This method sets the destination prefixes for sending messages and enables a simple
   * message broker for certain endpoints.</p>
   *
   * @param registry the {@link MessageBrokerRegistry} used to configure the message broker.
   *
   * <ul>
   *   <li><strong>setApplicationDestinationPrefixes:</strong> Defines the prefix used for sending messages
   *   from the client to the server. In this case, messages with the destination prefix "/chat"
   *   or "/auth-user" will be routed to message-handling methods in the server.</li>
   *
   *   <li><strong>enableSimpleBroker:</strong> Enables a simple in-memory message broker for routing
   *   messages back to clients. In this case, messages with destinations starting with "/chats" or "/user"
   *   will be handled by the message broker.</li>
   * </ul>
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes(CHAT_APPLICATION_DESTINATION, AUTH_USER_APPLICATION_DESTINATION);
    registry.enableSimpleBroker(CHATS_BROKER_DESTINATION, USER_BROKER_DESTINATION);
  }
}
