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

  User getAuthenticatedUser();

  /**
   * Saves or updates a JWT token for the specified user. Any existing tokens associated with the user
   * are deleted, and the new token is saved in the database.
   *
   * @param user The {@link User} entity for which the token is being generated.
   * @return The generated JWT token as a string.
   */
  String saveOrUpdateUserToken(User user);

  UserDetails getUserDetails(String token);

  void validateUserEmailAndPassword(User user);

  AuthResponse register(RegisterRequest request);

  void authenticateUser(String token, HttpServletRequest request);

}
