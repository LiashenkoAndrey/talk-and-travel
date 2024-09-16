package online.talkandtravel.controller.http;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.AuthenticationFacade;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.dto.auth.LoginRequest;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.user.OnlineStatusDto;
import online.talkandtravel.service.OnlineService;
import online.talkandtravel.util.constants.ApiPathConstants;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static online.talkandtravel.service.impl.OnlineServiceImpl.USERS_ONLINE_STATUS_ENDPOINT;

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

  @PostMapping("/register")
  public AuthResponse register(@RequestBody @Valid RegisterRequest dto) {
    AuthResponse response = authFacade.register(dto);
    notifyAllUserIsOnline(response);
    return response;
  }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody @Valid LoginRequest loginRequest) {
    AuthResponse response = authFacade.login(loginRequest);
    notifyAllUserIsOnline(response);
    return response;
  }

  private void notifyAllUserIsOnline(AuthResponse authResponse) {
    OnlineStatusDto statusDto = onlineService.updateUserOnlineStatus(authResponse.userDto().id(), true);
    messagingTemplate.convertAndSend(USERS_ONLINE_STATUS_ENDPOINT, statusDto);
  }
}
