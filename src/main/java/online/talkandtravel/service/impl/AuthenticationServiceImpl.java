package online.talkandtravel.service.impl;

import static java.lang.String.format;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.auth.UserAuthenticationException;
import online.talkandtravel.exception.auth.RegistrationException;
import online.talkandtravel.exception.user.UserNotAuthenticatedException;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.AuthResponse;
import online.talkandtravel.model.entity.Role;
import online.talkandtravel.model.entity.Token;
import online.talkandtravel.model.entity.TokenType;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.AvatarService;
import online.talkandtravel.service.TokenService;
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.mapper.UserMapper;
import online.talkandtravel.util.validator.PasswordValidator;
import online.talkandtravel.util.validator.UserEmailValidator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
 *   <li>{@link #manageUserTokens(User)} - Generates a JWT token, manages token validity, and stores
 *       the token.
 *   <li>{@link #validateUserRegistrationData(User)} - Validates registration data and checks for
 *       duplicate emails.
 *   <li>{@link #revokeAllUserTokens(User)} - Marks all existing tokens as expired and revoked for
 *       the user.
 *   <li>{@link #generateStandardAvatar(User)} - Creates and saves a default avatar for a new user.
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
  private final AvatarService avatarService;



  /**
   * gets User entity that stored in spring security
   */
  @Override
  public User getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object principal = authentication.getPrincipal();
    return getUserFromPrincipal(principal);
  }

  @Override
  public boolean isUserAuth() {
    log.info(" SecurityContextHolder.getContext().getAuthentication() {}",  SecurityContextHolder.getContext().getAuthentication());
    return SecurityContextHolder.getContext().getAuthentication() != null;
  }

  /**
   * authToken.setDetails -  used to populate the token with additional details about the web
   * authentication request, such as the IP address and session ID
   *
   * @param userDetails
   * @param request
   */
  @Override
  public void authenticateUser(UserDetails userDetails, HttpServletRequest request) {
    UsernamePasswordAuthenticationToken authToken =
        new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(authToken);
  }


  @Override
  @Transactional
  public AuthResponse register(User user) throws IOException {
    var newUser = registerNewUser(user);
    String jwtToken = manageUserTokens(newUser);
    return createNewAuthResponse(jwtToken, newUser);
  }

  @Override
  @Transactional
  public AuthResponse login(String email, String password) {
    var authenticatedUser = authenticateUser(email, password);
    String jwtToken = manageUserTokens(authenticatedUser);
    return createNewAuthResponse(jwtToken, authenticatedUser);
  }

  private User authenticateUser(String email, String password) {
    Optional<User> userOptional = userService.findUserByEmail(email.toLowerCase());

    if (userOptional.isPresent()) {
      User user = userOptional.get();
      checkUserCredentials(password, user);
      return user;
    }
    log.info("User with email - {} not found", email);
    throw new UserNotFoundException("User with email - " + email + " not found", "Bad credentials");
  }

  private User registerNewUser(User user) throws IOException {
    validateUserRegistrationData(user);
    var newUser = userService.save(createNewUser(user));
    generateStandardAvatar(newUser);
    return newUser;
  }

  private String manageUserTokens(User user) {
    String jwtToken = tokenService.generateToken(user);
    var token = createNewToken(jwtToken, user);
    revokeAllUserTokens(user);
    tokenService.deleteInvalidTokensByUserId(user.getId());
    tokenService.save(token);
    return jwtToken;
  }

  private void validateUserRegistrationData(User user) {
    String lowercaseEmail = user.getUserEmail().toLowerCase();
    user.setUserEmail(lowercaseEmail);
    validateEmailAndPassword(user);
    var userByEmail = userService.findUserByEmail(user.getUserEmail());
    checkForDuplicateEmail(userByEmail);
  }

  // todo: clarify an appointment of this method and create documentation

  private void revokeAllUserTokens(User user) {
    var validUserTokens = tokenService.findAllValidTokensByUserId(user.getId());
    if (validUserTokens.isEmpty()) {
      return;
    }
    validUserTokens.forEach(
        token -> {
          token.setExpired(true);
          token.setRevoked(true);
        });
    tokenService.saveAll(validUserTokens);
  }
  private void generateStandardAvatar(User savedUser) throws IOException {
    log.info("generateStandardAvatar: savedUser - {}", savedUser);
    var avatar = avatarService.createDefaultAvatar(savedUser.getUserName());
    avatar.setUser(savedUser);
    avatarService.save(avatar);
  }

  /**
   * checks if passed password doesn't match password stored in database - throw an exception
   *
   * @param password passed password
   * @param user user
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
   * In a spring security if a user not auth, principal equals 'anonymousUser' string
   * This method checks if principal is not {@link CustomUserDetails} class then throw an exception
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
