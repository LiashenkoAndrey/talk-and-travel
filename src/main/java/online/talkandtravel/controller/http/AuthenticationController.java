package online.talkandtravel.controller.http;

import static online.talkandtravel.util.constants.ApiPathConstants.USERS_ONLINE_STATUS_ENDPOINT;

import jakarta.validation.Valid;
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
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.service.OnlineService;
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.constants.ApiPathConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller class responsible for handling HTTP requests related to user authentication. This
 * includes user registration and login operations.
 *
 * <ul>
 *   <li>{@code register} - Handles user registration by mapping the DTO to a user model and
 *       delegating the registration process to the authentication service.
 *   <li>{@code login} - Authenticates a user based on their email and password, returning an
 *       authentication response upon success.
 * </ul>
 */
@RestController
@RequestMapping(ApiPathConstants.API_BASE_PATH + "/authentication")
@RequiredArgsConstructor
@Log4j2
public class AuthenticationController {

  private final AuthenticationFacade authFacade;
  private final OnlineService onlineService;
  private final SimpMessagingTemplate messagingTemplate;
  private final UserService userService;

  @PostMapping("/password-recovery")
  public ResponseEntity<?> recoverPassword(@RequestBody @Valid RecoverPasswordRequest request) {
    userService.checkUserExistByEmail(request.userEmail());
    authFacade.recoverPassword(request);
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  @PatchMapping("/password-recovery")
  public ResponseEntity<?> updatePassword(@RequestBody @Valid UpdatePasswordRequest request) {
    authFacade.updatePassword(request);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody @Valid LoginRequest loginRequest) {
    AuthResponse response = authFacade.login(loginRequest);
    notifyAllUserIsOnline(response);
    return response;
  }

  @PostMapping("/social/login")
  public AuthResponse socialLogin(@RequestBody @Valid SocialLoginRequest loginRequest) {
    AuthResponse response = authFacade.socialLogin(loginRequest);
    notifyAllUserIsOnline(response);
    return response;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest dto) {
    authFacade.onUserRegister(dto);
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  @PostMapping("/registration-confirmation")
  public ResponseEntity<AuthResponse> confirmRegistration(@RequestBody @Valid RegistrationConfirmationRequest request) {
    AuthResponse response = authFacade.confirmRegistration(request);
    notifyAllUserIsOnline(response);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/social/register")
  public ResponseEntity<AuthResponse> socialRegister(@RequestBody @Valid SocialRegisterRequest registerRequest) {
    AuthResponse response = authFacade.socialRegister(registerRequest);
    notifyAllUserIsOnline(response);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  private void notifyAllUserIsOnline(AuthResponse authResponse) {
    OnlineStatusDto statusDto = onlineService.updateUserOnlineStatus(authResponse.userDto().id(), true);
    messagingTemplate.convertAndSend(USERS_ONLINE_STATUS_ENDPOINT, statusDto);
  }
}
