package online.talkandtravel.service.impl;

import static java.lang.String.format;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.auth.RegistrationException;
import online.talkandtravel.exception.auth.UserAuthenticationException;
import online.talkandtravel.exception.user.UserNotAuthenticatedException;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.AuthResponse;
import online.talkandtravel.model.entity.Role;
import online.talkandtravel.model.entity.Token;
import online.talkandtravel.model.entity.TokenType;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.TokenRepository;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.TokenService;
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.mapper.UserMapper;
import online.talkandtravel.util.validator.PasswordValidator;
import online.talkandtravel.util.validator.UserEmailValidator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link AuthenticationService} for managing user authentication and
 * registration.
 *
 * <p>This service handles user registration and login processes, including:
 *
 * <ul>
 *   <li>{@link #getAuthenticatedUser()} - gets the authenticated
 *       user from {@link SecurityContextHolder}
 *   <li>{@link #register(User)} - Registers a new user, creates an authentication token, and
 *       generates a default avatar.
 *   <li>{@link #login(String, String)} - Authenticates a user based on email and password, and
 *       returns an authentication token.
 *   <li>{@link #authenticateUser(String, String)} - Validates user credentials and throws an
 *       exception if credentials are invalid.
 *   <li>{@link #registerNewUser(User)} - Validates and registers a new user, and generates a
 *       default avatar.
 *   <li>{@link #saveOrUpdateUserToken(User)} - Generates a JWT token, manages token validity, and stores
 *       the token.
 *   <li>{@link #validateUserRegistrationData(User)} - Validates registration data and checks for
 *       duplicate emails.
 *   <li>{@link #checkUserCredentials(String, User)} - Checks if the provided password matches the
 *       stored password.
 *   <li>{@link #checkForDuplicateEmail(Optional<User>)} - Throws an exception if a user with the
 *       same email already exists.
 *   <li>{@link #validateEmailAndPassword(User)} - Validates email and password formats.
 *   <li>{@link #createNewAuthResponse(String, User)} - Constructs an {@link AuthResponse}
 *       containing the JWT token and user DTO.
 *   <li>{@link #createNewToken(String, User)} - Creates a new {@link Token} object with the given
 *       JWT token and user details.
 *   <li>{@link #createNewUser(User)} - Builds a new {@link User} object with default settings for
 *       registration.
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationServiceImpl implements AuthenticationService {
  private final PasswordValidator passwordValidator;
  private final UserEmailValidator emailValidator;
  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;
  private final UserMapper userMapper;
  private final TokenRepository tokenRepository;
  private final UserDetailsService userDetailsService;

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
  public boolean isUserAuth() {
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
  public void authenticateUser(String token, HttpServletRequest request) {
    String email = tokenService.extractUsername(token);
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
    WebAuthenticationDetails details = new WebAuthenticationDetailsSource().buildDetails(request);

    var authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
        userDetails.getAuthorities());

    // populate the token with additional details from the HTTP request
    authToken.setDetails(details);
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }

  /**
   * Registers a new user in the system by validating the user data, saving the user,
   * and generating a JWT token.
   *
   * <p>This method is transactional to ensure that user registration and token generation
   * are performed atomically.
   *
   * @param user The {@link User} entity containing registration data.
   * @return An {@link AuthResponse} containing the generated JWT token and user details.
   * @throws IOException If an error occurs during user registration.
   */
  @Override
  @Transactional
  public AuthResponse register(User user) throws IOException {
    var newUser = registerNewUser(user);
    String jwtToken = saveOrUpdateUserToken(newUser);
    return createNewAuthResponse(jwtToken, newUser);
  }


  /**
   * Authenticates an existing user by verifying their email and password, and then generates
   * a JWT token for the authenticated user.
   *
   * <p>This method is transactional to ensure that authentication and token generation
   * are performed atomically.
   *
   * @param email The email of the user attempting to log in.
   * @param password The password of the user attempting to log in.
   * @return An {@link AuthResponse} containing the generated JWT token and user details.
   */
  @Override
  @Transactional
  public AuthResponse login(String email, String password) {
    var authenticatedUser = authenticateUser(email, password);
    String jwtToken = saveOrUpdateUserToken(authenticatedUser);
    return createNewAuthResponse(jwtToken, authenticatedUser);
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
  private User authenticateUser(String email, String password) {
    Optional<User> userOptional = userService.findUserByEmail(email.toLowerCase());
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      checkUserCredentials(password, user);
      return user;
    }
    throw new UserNotFoundException("User with email - " + email + " not found", "Bad credentials");
  }

  /**
   * Registers a new user by validating the user's registration data and saving the user entity.
   *
   * @param user The {@link User} entity containing registration data.
   * @return The saved {@link User} entity.
   * @throws IOException If an error occurs during user registration.
   */
  private User registerNewUser(User user) throws IOException {
    validateUserRegistrationData(user);
    return userService.save(createNewUser(user));
  }

  /**
   * Saves or updates a JWT token for the specified user. Any existing tokens associated with the user
   * are deleted, and the new token is saved in the database.
   *
   * @param user The {@link User} entity for which the token is being generated.
   * @return The generated JWT token as a string.
   */
  private String saveOrUpdateUserToken(User user) {
    String jwtToken = tokenService.generateToken(user);
    var token = createNewToken(jwtToken, user);
    tokenRepository.deleteAllByUserId(user.getId());
    tokenService.save(token);
    return jwtToken;
  }

  private void validateUserRegistrationData(User user) {
    String lowercaseEmail = user.getUserEmail().toLowerCase();
    user.setUserEmail(lowercaseEmail);
    validateEmailAndPassword(user);
    Optional<User> userByEmail = userService.findUserByEmail(user.getUserEmail());
    checkForDuplicateEmail(userByEmail);
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
      throw new UserAuthenticationException("Incorrect username or password!!!");
    }
  }

  private void checkForDuplicateEmail(Optional<User> user) {
    if (user.isPresent()) {
      throw new RegistrationException("A user with this email already exists");
    }
  }

  private void validateEmailAndPassword(User user) {
    if (!emailValidator.isValid(user.getUserEmail())) {
      throw new RegistrationException("Invalid email address");
    }
    if (!passwordValidator.isValid(user.getPassword())) {
      throw new RegistrationException(
          "Passwords must be 8 to 16 characters long and contain "
              + "at least one letter, one digit, and one special character.");
    }
  }

  private AuthResponse createNewAuthResponse(String jwtToken, User user) {
    var userDto = userMapper.mapToShortDto(user);
    return AuthResponse.builder().token(jwtToken).userDto(userDto).build();
  }

  private Token createNewToken(String jwtToken, User savedUser) {
    return Token.builder()
        .user(savedUser)
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
  }

  private User createNewUser(User user) {
    return User.builder()
        .userName(user.getUserName())
        .userEmail(user.getUserEmail().toLowerCase())
        .password(user.getPassword())
        .role(Role.USER)
        .build();
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
