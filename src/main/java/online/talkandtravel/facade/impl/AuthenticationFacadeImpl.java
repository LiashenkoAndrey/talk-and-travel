package online.talkandtravel.facade.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.AuthenticationFacade;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.dto.auth.LoginRequest;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.Token;
import online.talkandtravel.model.entity.TokenType;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.TokenService;
import online.talkandtravel.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class AuthenticationFacadeImpl implements AuthenticationFacade {

  private final UserService userService;
  private final TokenService tokenService;
  private final AuthenticationService authenticationService;

  @Override
  @Transactional
  public AuthResponse login(LoginRequest request) {
    log.info("Login - email {}", request.userEmail());
    User authenticatedUser = authenticationService.checkUserCredentials(request.userEmail(), request.password());
    userService.updateLastLoggedOnToNow(authenticatedUser);
    String jwtToken = saveOrUpdateUserToken(authenticatedUser.getId());
    return new AuthResponse(jwtToken, userService.mapToUserDtoBasic(authenticatedUser));
  }

  @Override
  public AuthResponse register(RegisterRequest request) {
    log.info("register user - name: {}, email: {}", request.userName(), request.userEmail());
    UserDtoBasic newUser = validateAndSaveNewUser(request);
    String jwtToken = saveOrUpdateUserToken(newUser.id());
    return new AuthResponse(jwtToken, newUser);
  }

  @Override
  public boolean isUserAuthenticated() {
    return authenticationService.isUserAuthenticated();
  }

  /**
   * Saves or updates a JWT token for the specified user. Any existing tokens associated with the user
   * are deleted, and the new token is saved in the database.
   *
   * @return The generated JWT token as a string.
   */
  @Override
  public String saveOrUpdateUserToken(Long userId) {
    String jwtToken = tokenService.generateToken(userId);
    var token = createNewToken(jwtToken, userId);
    tokenService.deleteUserToken(userId);
    tokenService.save(token);
    return jwtToken;
  }

  @Override
  public void authenticateUser(String token, HttpServletRequest request) {
    UserDetails userDetails = getUserDetails(token);
    authenticationService.authenticateUser(userDetails, request);
  }

  @Override
  public UsernamePasswordAuthenticationToken createUsernamePasswordAuthenticationToken(
      String token) {
    UserDetails userDetails = getUserDetails(token);
    return authenticationService.createUsernamePasswordAuthenticationToken(userDetails);
  }

  private UserDtoBasic validateAndSaveNewUser(RegisterRequest request) {
    validateUserRegistrationData(request.userEmail(), request.password());
    return userService.createAndSaveNewUser(request);
  }

  private Token createNewToken(String jwtToken, Long userId) {
    return Token.builder()
        .user(userService.getReferenceById(userId))
        .token(jwtToken)
        .tokenType(TokenType.BEARER)
        .expired(false)
        .revoked(false)
        .build();
  }

  private void validateUserRegistrationData(String email, String password) {
    authenticationService.validateUserEmailAndPassword(email, password);
    authenticationService.checkForDuplicateEmail(email);
  }

  private UserDetails getUserDetails(String token) {
    Long userId = tokenService.extractId(token);
    return userService.getUserDetails(userId);
  }
}
