package online.talkandtravel.config.websocket;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.model.HttpException;
import online.talkandtravel.exception.token.AuthenticationHeaderIsInvalidException;
import online.talkandtravel.service.TokenService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * class that handles a connection of websocket
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class OnConnectChannelInterceptor implements ChannelInterceptor {

  private final TokenService tokenService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    try {
      final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
          StompHeaderAccessor.class);

      if (StompCommand.CONNECT.equals(accessor.getCommand())) {
        onConnect(accessor);
      }
      return message;
    } catch (HttpException httpException) {
      log.error(httpException.getMessage(), httpException);
      throw new MessageDeliveryException(message, httpException.getMessageToClient(), httpException);

    } catch (Exception e) {
      log.error(e.getMessage(), e);
      throw new MessageDeliveryException(message, e);
    }
  }

  private void onConnect(StompHeaderAccessor accessor) {
    String authHeader = accessor.getFirstNativeHeader("Authorization");
    String token = getTokenFromAuthHeader(authHeader);
    tokenService.validateToken(token);
    UsernamePasswordAuthenticationToken authenticationToken = auth(token);
    accessor.setUser(authenticationToken);
    log.info("Authentication is successful");
  }

  private UsernamePasswordAuthenticationToken auth(String token) {
    return new UsernamePasswordAuthenticationToken(
        tokenService.extractUsername(token), null,
        Collections.singleton((GrantedAuthority) () -> "USER")
    );
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
