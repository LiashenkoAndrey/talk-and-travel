package online.talkandtravel.controller.http;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.AuthenticationFacade;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.dto.auth.LoginRequest;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.entity.UserOnlineStatus;
import online.talkandtravel.service.event.UserEventService;
import online.talkandtravel.util.constants.ApiPathConstants;
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
  private final UserEventService userEventService;

  @PostMapping("/register")
  public AuthResponse register(@RequestBody @Valid RegisterRequest dto) {
    AuthResponse response = authFacade.register(dto);
    userEventService.updateUserOnlineStatus(UserOnlineStatus.ONLINE, response.userDto().id());
    return response;
  }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody @Valid LoginRequest loginRequest) {
    AuthResponse response = authFacade.login(loginRequest);
    userEventService.updateUserOnlineStatus(UserOnlineStatus.ONLINE, response.userDto().id());
    return response;
  }
}
