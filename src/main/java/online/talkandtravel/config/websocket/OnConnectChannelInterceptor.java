package online.talkandtravel.config.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.model.WebSocketException;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.TokenService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * class that handles a connection of websocket
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class OnConnectChannelInterceptor implements ChannelInterceptor {

  private final AuthenticationService authenticationService;
  private final TokenService tokenService;
  private final UserDetailsService userDetailsService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel channel) {
    StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String authHeader = accessor.getFirstNativeHeader("Authorization");
      String token = getTokenFromAuthHeader(authHeader);
      log.info("Authorization header: {}", authHeader);
      auth(token);
    }
    return message;
  }

  private void auth(String token) {
    try {
      throwIfTokenNotValidOrUserIsAuth(token);
      UserDetails userDetails = userDetailsService.loadUserByUsername(
          tokenService.extractUsername(token));

      UsernamePasswordAuthenticationToken authToken =
          new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authToken);

      log.info("Success auth!");
    } catch (Exception e) {
      log.error(e);
      throw new RuntimeException(e);
    }
  }

  private String getTokenFromAuthHeader(String authHeader) {
    ifNotValidAuthenticationHeaderThrow(authHeader);
    return authHeader.substring(7);
  }

  private void ifNotValidAuthenticationHeaderThrow(String authHeader) {
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      throw new RuntimeException("not valid auth header " + authHeader);
    }
  }

  private void throwIfTokenNotValidOrUserIsAuth(String token) {
    if (!tokenService.isValidToken(token) || !authenticationService.isUserAuth()) {
      throw new RuntimeException("not valid token or not auth");
    }
  }

}
