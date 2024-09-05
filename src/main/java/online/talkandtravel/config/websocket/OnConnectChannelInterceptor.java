package online.talkandtravel.config.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.token.AuthenticationHeaderIsInvalidException;
import online.talkandtravel.facade.AuthenticationFacade;
import online.talkandtravel.service.TokenService;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

/** class that handles a connection of websocket */
@Component
@Log4j2
@RequiredArgsConstructor
public class OnConnectChannelInterceptor implements ChannelInterceptor {

  private final TokenService tokenService;
  private final AuthenticationFacade authenticationFacade;

  @Override
  public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
    try {
      final StompHeaderAccessor accessor =
          MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

      assert accessor != null;
      if (StompCommand.CONNECT.equals(accessor.getCommand())) {
        onConnect(accessor);
      }
      return message;
    } catch (Exception e) {
      log.error("Exception during Authentication WebSocket connection: {}", e.getMessage());
      throw new MessageDeliveryException(e.getMessage());
    }
  }

  private void onConnect(StompHeaderAccessor accessor) {
    String authHeader = accessor.getFirstNativeHeader("Authorization");
    String token = getTokenFromAuthHeader(authHeader);
    Long userId = tokenService.validateTokenAndGetUserId(token);

    UsernamePasswordAuthenticationToken authenticationToken =
        authenticationFacade.createUsernamePasswordAuthenticationToken(token);

    accessor.setUser(authenticationToken);
    log.info("Authentication is successful for user with id {}" , userId);
  }


  private String getTokenFromAuthHeader(String authHeader) {
    throwIfNotValidAuthenticationHeader(authHeader);
    return authHeader.substring(7);
  }

  private void throwIfNotValidAuthenticationHeader(String authHeader) {
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      throw new AuthenticationHeaderIsInvalidException(authHeader);
    }
  }
}
