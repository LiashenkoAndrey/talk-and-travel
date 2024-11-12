package online.talkandtravel.facade;

import jakarta.servlet.http.HttpServletRequest;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.dto.auth.LoginRequest;
import online.talkandtravel.model.dto.auth.RecoverPasswordRequest;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.auth.RegistrationConfirmationRequest;
import online.talkandtravel.model.dto.auth.SocialLoginRequest;
import online.talkandtravel.model.dto.auth.SocialRegisterRequest;
import online.talkandtravel.model.dto.auth.UpdatePasswordRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface AuthenticationFacade {

  void recoverPassword(RecoverPasswordRequest request);

  void updatePassword(UpdatePasswordRequest request);

  AuthResponse login(LoginRequest request);

  AuthResponse socialLogin(SocialLoginRequest request);

  String saveOrUpdateUserToken(Long userId);

  AuthResponse socialRegister(SocialRegisterRequest request);

  boolean isUserAuthenticated();

  void authenticateUser(String token, HttpServletRequest request);

  UsernamePasswordAuthenticationToken createUsernamePasswordAuthenticationToken(String token);

  AuthResponse confirmRegistration(RegistrationConfirmationRequest request);

  void onUserRegister(RegisterRequest dto);
}
