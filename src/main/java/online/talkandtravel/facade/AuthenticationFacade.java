package online.talkandtravel.facade;

import jakarta.servlet.http.HttpServletRequest;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.dto.auth.LoginRequest;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoWithAvatarAndPassword;
import online.talkandtravel.model.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticationFacade {

  AuthResponse login(LoginRequest request);

  String saveOrUpdateUserToken(Long userId);

  AuthResponse register(RegisterRequest request);

  boolean isUserAuthenticated();

  void authenticateUser(String token, HttpServletRequest request);

}
