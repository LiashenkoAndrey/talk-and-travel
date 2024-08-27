package online.talkandtravel.service;

import jakarta.servlet.http.HttpServletRequest;
import online.talkandtravel.model.dto.AuthResponse;
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
 *   <li>{@link #register(User)} - Registers a new user with the provided {@link User} details. This
 *       method handles user creation, including password encryption and any necessary setup. It
 *       returns an {@link AuthResponse} containing information about the authentication result.
 *   <li>{@link #login(String, String)} - Authenticates a user using their email and password. If
 *       the credentials are valid, it returns an {@link AuthResponse} containing authentication
 *       details.
 * </ul>
 */
public interface AuthenticationService {


  User getAuthenticatedUser();

  boolean isUserAuthenticated();


  User checkUserCredentials(String email, String password);

  void authenticateUser(UserDetails userDetails, HttpServletRequest request);

}
