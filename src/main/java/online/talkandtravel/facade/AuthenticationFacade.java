package online.talkandtravel.facade;

import jakarta.servlet.http.HttpServletRequest;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.dto.auth.LoginRequest;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface AuthenticationFacade {

  AuthResponse login(LoginRequest request, HttpServletRequest httpServletRequest);

  String saveOrUpdateUserToken(Long userId);

  AuthResponse register(RegisterRequest request);

  boolean isUserAuthenticated();

  void authenticateUser(String token, HttpServletRequest request);

  UsernamePasswordAuthenticationToken createUsernamePasswordAuthenticationToken(String token);
}
