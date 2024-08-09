package online.talkandtravel.config;

import org.springframework.context.annotation.Configuration;
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
 * @param registry {@link StompEndpointRegistry} to register STOMP endpoints.
 * @param registry {@link MessageBrokerRegistry} to configure the message broker.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  private final String[] ALLOWED_URL =
      new String[] {
        "http://localhost:3001",
        "http://localhost:3000",
        "http://localhost:5500",
        "https://oleksandrprokopenkodev.github.io",
        "http://localhost:63342",
        "http://localhost:8080",
        "https://reginavarybrus.github.io"
      };

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws").setAllowedOriginPatterns(ALLOWED_URL).withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/chat");
    registry.enableSimpleBroker("/countries", "/group-messages");
  }
}
