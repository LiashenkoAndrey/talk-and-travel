package online.talkandtravel.config.websocket;

import static online.talkandtravel.util.constants.ApiPathConstants.APPLICATION_DESTINATION_PREFIX;
import static online.talkandtravel.util.constants.ApiPathConstants.BROKER_DESTINATION_PREFIX;

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
   * Configures the message broker for handling WebSocket messages.
   * <p>
   * This method sets the application destination prefixes that are used to route
   * messages from clients to the server, and enables a simple message broker
   * to route messages from the server back to clients.
   * </p>
   * <ul>
   *   <li>Application destination prefix - /request</li>
   *   <li>Broker Prefix - /notify</li>
   * </ul>
   *
   * @param registry the {@link MessageBrokerRegistry} used to configure the message broker.
   *                 It allows setting destination prefixes for application destinations
   *                 and enabling the message broker.
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes(APPLICATION_DESTINATION_PREFIX);
    registry.enableSimpleBroker(BROKER_DESTINATION_PREFIX);
  }
}
