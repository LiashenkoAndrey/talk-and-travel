package online.talkandtravel.facade;

import jakarta.servlet.http.HttpServletRequest;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.dto.auth.LoginRequest;
import online.talkandtravel.model.dto.auth.RegisterRequest;

public interface AuthenticationFacade {

  AuthResponse login(LoginRequest request);

  String saveOrUpdateUserToken(Long userId);

  AuthResponse register(RegisterRequest request);

  boolean isUserAuthenticated();

  void authenticateUser(String token, HttpServletRequest request);

}
