package online.talkandtravel.facade.impl;

import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.AuthenticationFacade;
import online.talkandtravel.model.dto.AuthResponse;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoWithAvatarAndPassword;
import online.talkandtravel.model.entity.Role;
import online.talkandtravel.model.entity.Token;
import online.talkandtravel.model.entity.TokenType;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.TokenService;
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 *   <li>{@link #registerUser(User)} - Validates and registers a new user, and generates a
 *       default avatar.
 *   <li>{@link #validateUserRegistrationData(User)} - Validates registration data and checks for
 *       duplicate emails.
 *   <li>{@link #validateUserEmailAndPassword(User)} - Validates email and password formats.
 */
@Component
@Log4j2
@RequiredArgsConstructor
public class AuthenticationFacadeImpl implements AuthenticationFacade {

  private final UserService userService;
  private final TokenService tokenService;
  private final AuthenticationService authenticationService;
  private final UserMapper userMapper;

  @Override
  public UpdateUserResponse update(UpdateUserRequest request) {
    User existingUser = getAuthenticatedUser();
    return userService.update(request, existingUser);
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
  public AuthResponse login(String email, String password) {
    var authenticatedUser = authenticationService.checkUserCredentials(email, password);
    String jwtToken = saveOrUpdateUserToken(authenticatedUser);
    return createNewAuthResponse(jwtToken, authenticatedUser);
  }

  private AuthResponse createNewAuthResponse(String jwtToken, User user) {
    var userDto = userMapper.mapToBasicDto(user);
    return new AuthResponse(jwtToken, userDto);
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
  public AuthResponse register(UserDtoWithAvatarAndPassword request) {
    log.info("register - {}", request);
    var user = userMapper.mapToUserWithPassword(request);

    var newUser = registerUser(createNewUser(user));
    String jwtToken = saveOrUpdateUserToken(newUser);
    return createNewAuthResponse(jwtToken, newUser);
  }

  private User createNewUser(User user) {
    return User.builder()
        .userName(user.getUserName())
        .userEmail(user.getUserEmail().toLowerCase())
        .password(user.getPassword())
        .role(Role.USER)
        .build();
  }

  @Override
  public User getAuthenticatedUser() {
    return authenticationService.getAuthenticatedUser();
  }

  /**
   * Saves or updates a JWT token for the specified user. Any existing tokens associated with the user
   * are deleted, and the new token is saved in the database.
   *
   * @param user The {@link User} entity for which the token is being generated.
   * @return The generated JWT token as a string.
   */
  @Override
  public String saveOrUpdateUserToken(User user) {
    String jwtToken = tokenService.generateToken(user);
    var token = createNewToken(jwtToken, user);
    tokenService.deleteUserToken(user.getId());
    tokenService.save(token);
    return jwtToken;
  }

  @Override
  public UserDetails getUserDetails(String token) {
    Long userId = tokenService.extractId(token);
    return userService.getUserDetails(userId);
  }

  /**
   * Registers a new user by validating the user's registration data and saving the user entity.
   *
   * @param user The {@link User} entity containing registration data.
   * @return The saved {@link User} entity.
   */
  @Override
  public User registerUser(User user) {
    validateUserRegistrationData(user);
    return userService.save(user);
  }

  @Override
  public void validateUserEmailAndPassword(User user) {
    userService.validateUserEmailAndPassword(user);
  }

  @Override
  public void authenticateUser(String token, HttpServletRequest request) {
    UserDetails userDetails = getUserDetails(token);
    authenticationService.authenticateUser(userDetails, request);
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

  private void validateUserRegistrationData(User user) {
    userService.validateUserEmailAndPassword(user);
    userService.checkForDuplicateEmail(user.getUserEmail());
  }
}
