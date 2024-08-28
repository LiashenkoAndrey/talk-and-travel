package online.talkandtravel.service;

import jakarta.servlet.http.HttpServletRequest;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for handling user authentication and registration operations.
 *
 * <p>This service is responsible for user registration and login processes, providing mechanisms to
 * create new user accounts and authenticate existing users, retrieving authenticated user from
 * spring security context.
 *
 * <p>Methods:
 *
 * <ul>
 *    <li>{@link #getAuthenticatedUser} gets the authenticated user
 *       details.
 * </ul>
 */
public interface AuthenticationService {

  void validateUserEmailAndPassword(User user);

  void checkForDuplicateEmail(String userEmail);

  User getAuthenticatedUser();

  boolean isUserAuthenticated();

  User checkUserCredentials(String email, String password);

  void authenticateUser(UserDetails userDetails, HttpServletRequest request);

}
