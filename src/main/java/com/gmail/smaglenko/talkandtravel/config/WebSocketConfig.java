package com.gmail.smaglenko.talkandtravel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
  private final String[] ALLOWED_URL =
      new String[] {
        "http://localhost:3001",
        "http://localhost:3000",
        "http://localhost:5500",
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
