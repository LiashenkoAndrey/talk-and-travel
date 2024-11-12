package online.talkandtravel.service.impl;

import static java.lang.String.format;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.auth.UserAuthenticationException;
import online.talkandtravel.exception.user.UserAlreadyExistsException;
import online.talkandtravel.exception.user.UserNotAuthenticatedException;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.AuthenticationService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link AuthenticationService} for managing user authentication and
 * registration.
 *
 * <p>This service handles user registration and login processes, including:
 *
 * <ul>
 *   <li>{@link #getAuthenticatedUser()} - gets the authenticated
 *       user from {@link SecurityContextHolder}
 *   <li>{@link #checkUserCredentials(String, String)} - Validates user credentials and throws an
 *       exception if credentials are invalid.
 *   <li>{@link #checkUserCredentials(String, User)} - Checks if the provided password matches the
 *       stored password.
 *       registration.
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationServiceImpl implements AuthenticationService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  /**
   * Retrieves the authenticated user from the Spring Security context.
   *
   * <p>This method extracts the authentication object from the security context
   * and retrieves the principal. It then attempts to convert the principal
   * into a {@link User} entity.
   *
   * @return The authenticated {@link User} entity.
   * @throws UserNotAuthenticatedException if the principal is not an instance of {@link CustomUserDetails}.
   */
  @Override
  public User getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object principal = authentication.getPrincipal();
    return getUserFromPrincipal(principal);
  }

  /**
   * Checks if the user is authenticated by verifying the presence of an authentication object
   * in the Spring Security context.
   *
   * @return {@code true} if an authentication object is present; {@code false} otherwise.
   */
  @Override
  public boolean isUserAuthenticated() {
    return SecurityContextHolder.getContext().getAuthentication() != null;
  }

  /**
   * Authenticates the user by setting the authentication object in the Spring Security context.
   *
   * <p>This method creates a {@link UsernamePasswordAuthenticationToken} using the provided
   * {@link UserDetails}, and populates the token with additional details from the HTTP request,
   * such as the IP address and session ID.
   *
   * @param request The HTTP servlet request containing additional authentication details.
   */
  @Override
  public void authenticateUser(UserDetails userDetails, HttpServletRequest request) {
    var authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
        userDetails.getAuthorities());

    if (request != null) {
      // populate the token with additional details from the HTTP request
      WebAuthenticationDetails details = new WebAuthenticationDetailsSource().buildDetails(request);
      authToken.setDetails(details);
    }
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

  @Override
  public UsernamePasswordAuthenticationToken createUsernamePasswordAuthenticationToken(
      UserDetails userDetails) {
    return new UsernamePasswordAuthenticationToken(
        userDetails,
        null,
        userDetails.getAuthorities()
    );
  }

  /**
   * Authenticates a user by their email and password. If the user is found and the credentials
   * match, the user is returned.
   *
   * @param email The email of the user attempting to log in.
   * @param password The password of the user attempting to log in.
   * @return The authenticated {@link User}.
   * @throws UserNotFoundException if the user is not found or the credentials are incorrect.
   */
  @Override
  public User checkUserCredentials(String email, String password) {
    User user = getUser(email);
    checkUserCredentials(password, user);
    return user;
  }

  @Override
  public User getRegisteredUser(String email) {
    return getUser(email);
  }


  private User getUser(String email) {
    return userRepository.findByUserEmail(email)
        .orElseThrow(
            () -> new UserAuthenticationException("user with email %s not found".formatted(email)));
  }

  /**
   * Verifies the provided password against the stored password for the user.
   *
   * @param password The password provided during login.
   * @param user The {@link User} entity whose credentials are being verified.
   * @throws UserAuthenticationException if the provided password does not match the stored password.
   */
  private void checkUserCredentials(String password, User user) {
    if (!passwordEncoder.matches(password, user.getPassword())) {
      throw new UserAuthenticationException("Provided password and stored password don't match");
    }
  }

  /**
   * Retrieves a {@link User} entity from the given principal object. If the principal is not an
   * instance of {@link CustomUserDetails}, an exception is thrown.
   *
   * @param principal The principal object from the security context.
   * @return The {@link User} entity associated with the principal.
   * @throws UserNotAuthenticatedException if the principal is not a valid {@link CustomUserDetails}
   *                                       instance.
   */
  private User getUserFromPrincipal(Object principal) {
    ifPrincipalIsStringThrowException(principal);
    verifyPrincipal(principal);
    CustomUserDetails userDetails = (CustomUserDetails) principal;
    return userDetails.getUser();
  }

  /**
   * This method checks if a principal is not a {@link CustomUserDetails} class then throw an
   * exception
   */
  private void verifyPrincipal(Object principal) {
    if (!(principal instanceof CustomUserDetails)) {
      throw new UserNotAuthenticatedException(
          format("Principal:%s - is not an instance of a CustomUserDetails", principal),
          "Unexpected exception!"
      );
    }
  }

  /**
   * If a principal is a string then throw an exception
   */
  private void ifPrincipalIsStringThrowException(Object principal) {
    if (principal instanceof String) {
      throw new UserNotAuthenticatedException("principal is a string ");
    }
  }
}
