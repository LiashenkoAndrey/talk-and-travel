package online.talkandtravel.config.websocket;

import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.TokenService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
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
    final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message,
        StompHeaderAccessor.class);

    if (StompCommand.CONNECT.equals(accessor.getCommand())) {
      String authHeader = accessor.getFirstNativeHeader("Authorization");
      String token = getTokenFromAuthHeader(authHeader);
      UsernamePasswordAuthenticationToken authenticationToken = auth(token);
      accessor.setUser(authenticationToken);

    }
    return message;
  }

  private UsernamePasswordAuthenticationToken auth(String token) {
    throwIfTokenNotValidOrUserIsAuth(token);
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
      throw new RuntimeException("not valid auth header " + authHeader);
    }
  }

  private void throwIfTokenNotValidOrUserIsAuth(String token) {
    if (!tokenService.isValidToken(token) || authenticationService.isUserAuth()) {
      throw new RuntimeException("not valid token or not auth");
    }
  }

}
