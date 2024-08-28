package online.talkandtravel.facade.impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.AuthenticationFacade;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.dto.auth.LoginRequest;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoBasic;
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
  public AuthResponse login(LoginRequest request) {
    log.info("Login - email {}", request.userEmail());
    User authenticatedUser = authenticationService.checkUserCredentials(request.userEmail(), request.password());
    String jwtToken = saveOrUpdateUserToken(authenticatedUser);
    return createNewAuthResponse(jwtToken, authenticatedUser);
  }

  @Override
  public AuthResponse register(RegisterRequest request) {
    log.info("register user - {}", request);
    User newUser = createAndSaveNewUser(request);
    String jwtToken = saveOrUpdateUserToken(newUser);
    return createNewAuthResponse(jwtToken, newUser);
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

  @Override
  public void validateUserEmailAndPassword(User user) {
    authenticationService.validateUserEmailAndPassword(user);
  }

  @Override
  public void authenticateUser(String token, HttpServletRequest request) {
    UserDetails userDetails = getUserDetails(token);
    authenticationService.authenticateUser(userDetails, request);
  }

  private AuthResponse createNewAuthResponse(String jwtToken, User user) {
    var userDto = userMapper.mapToBasicDto(user);
    return new AuthResponse(jwtToken, userDto);
  }

  private User createAndSaveNewUser(RegisterRequest request) {
    User user = createNewUser(request);
    validateUserRegistrationData(user);
    return userService.save(user);
  }

  private User createNewUser(RegisterRequest request) {
    return User.builder()
        .userName(request.userName())
        .userEmail(request.userEmail())
        .password(request.userPassword())
        .role(Role.USER)
        .build();
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
    authenticationService.validateUserEmailAndPassword(user);
    authenticationService.checkForDuplicateEmail(user.getUserEmail());
  }
}
