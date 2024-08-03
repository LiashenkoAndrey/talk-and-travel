package online.talkandtravel.testingConfig;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class for testing purposes
 */
@Configuration
@Profile("test")
@EnableWebSocketMessageBroker
public class WSConfig implements WebSocketMessageBrokerConfigurer {

  /**
   * is the HTTP URL for the endpoint to which a WebSocket (or SockJS)
   * client needs to connect for the WebSocket handshake.
   * @param registry StompEndpointRegistry
   */
  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }

  /**
   * STOMP messages whose destination header begins with /chat are routed
   * to @MessageMapping methods in @Controller classes
   * Use the built-in message broker for subscriptions and broadcasting and route
   * messages whose destination header begins with /countries or /group-messages to the broker.
   * @param registry MessageBrokerRegistry
   */
  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    registry.setApplicationDestinationPrefixes("/chat"); //send
    registry.enableSimpleBroker("/countries", "/group-messages"); // subscribe
  }
}