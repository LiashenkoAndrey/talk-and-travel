package online.talkandtravel.facade.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.AuthenticationFacade;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.dto.auth.LoginRequest;
import online.talkandtravel.model.dto.auth.RecoverPasswordRequest;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.auth.RegistrationConfirmationRequest;
import online.talkandtravel.model.dto.auth.SocialLoginRequest;
import online.talkandtravel.model.dto.auth.SocialRegisterRequest;
import online.talkandtravel.model.dto.auth.UpdatePasswordRequest;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.Token;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.MailService;
import online.talkandtravel.service.TokenService;
import online.talkandtravel.service.UserService;
import org.springframework.scheduling.annotation.Async;
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
  private final MailService mailService;

  @Override
  @Async
  public void recoverPassword(RecoverPasswordRequest request) {
    log.info("Recover user password, email: {}", request.userEmail());
    String email = request.userEmail();
    User user = userService.getUser(email);
    Token recoveryToken = tokenService.generatePasswordRecoveryToken(user);

    mailService.sendPasswordRecoverMessage(email, recoveryToken.getToken());
  }

  @Override
  public void updatePassword(UpdatePasswordRequest request) {
    log.info("Update user password");
    String tokenStr = request.token();
    Token token = tokenService.getToken(tokenStr);
    tokenService.checkTokenIsExpired(token);
    userService.updateUserPassword(token.getUser(), request.newPassword());
    tokenService.deleteToken(token);
  }

  @Override
  @Transactional
  public AuthResponse login(LoginRequest request) {
    log.info("Login - email {}", request.userEmail());
    User authenticatedUser = authenticationService.checkUserCredentials(request.userEmail(), request.password());
    return processSuccessfulLogin(authenticatedUser);
  }

  @Override
  @Transactional
  public AuthResponse socialLogin(SocialLoginRequest request) {
    log.info("Login using social - email {}", request.userEmail());
    User authenticatedUser = authenticationService.getRegisteredUser(request.userEmail());
    return processSuccessfulLogin(authenticatedUser);
  }

  @Override
  public AuthResponse socialRegister(SocialRegisterRequest request) {
    log.info("Register user from social - name: {}, email: {}", request.userName(), request.userEmail());
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
    User user = userService.getUserById(userId);
    var token = tokenService.saveNewToken(jwtToken, user);
    tokenService.deleteUserToken(user);
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

  @Override
  public AuthResponse confirmRegistration(RegistrationConfirmationRequest request) {
    log.info("confirm user registration");
    RegisterRequest registerRequest = userService.getUserRegisterDataFromTempStorage(request.token());
    return register(registerRequest);
  }

  @Override
  @Async
  public void onUserRegister(RegisterRequest request) {
    log.info("Register user: email: {}, name: {}", request.userEmail(), request.userName());
    validateUserRegistrationData(request.userEmail());
    String token = UUID.randomUUID().toString();
    log.info("token: {}", token);

    userService.saveUserRegisterDataToTempStorage(token, request);
    mailService.sendConfirmRegistrationMessage(request.userEmail(), token);
  }

  private AuthResponse register(RegisterRequest request) {
    log.info("register user - name: {}, email: {}", request.userName(), request.userEmail());
    UserDtoBasic newUser = userService.saveNewUser(request);
    String jwtToken = saveOrUpdateUserToken(newUser.id());
    return new AuthResponse(jwtToken, newUser);
  }

  private AuthResponse processSuccessfulLogin(User authenticatedUser) {
    userService.updateLastLoggedOnToNow(authenticatedUser);
    String jwtToken = saveOrUpdateUserToken(authenticatedUser.getId());
    return new AuthResponse(jwtToken, userService.mapToUserDtoBasic(authenticatedUser));
  }

  private UserDtoBasic validateAndSaveNewUser(SocialRegisterRequest request) {
    validateUserRegistrationData(request.userEmail());
    return userService.saveNewUser(request);
  }

  private void validateUserRegistrationData(String email) {
    userService.checkForDuplicateEmail(email);
  }

  private UserDetails getUserDetails(String token) {
    Long userId = tokenService.extractId(token);
    return userService.getUserDetails(userId);
  }
}
