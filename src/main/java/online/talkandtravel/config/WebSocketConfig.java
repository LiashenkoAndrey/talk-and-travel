package online.talkandtravel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration class that enables and configures STOMP support in an application
 */
@Configuration
@EnableWebSocketMessageBroker
@Profile("dev")
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final String[] ALLOWED_URL = new String[]{
            "http://localhost:3001",
            "http://localhost:3000",
            "http://localhost:63342",
            "http://localhost:8080",
            "https://reginavarybrus.github.io"
    };

    /**
     * is the HTTP URL for the endpoint to which a WebSocket (or SockJS)
     * client needs to connect for the WebSocket handshake.
     * @param registry StompEndpointRegistry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(ALLOWED_URL)
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
        registry.setApplicationDestinationPrefixes("/chat");
        registry.enableSimpleBroker("/countries", "/group-messages");
    }
}
