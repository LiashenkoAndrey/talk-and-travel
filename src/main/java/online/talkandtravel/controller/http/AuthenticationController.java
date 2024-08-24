package online.talkandtravel.controller.http;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.AuthResponse;
import online.talkandtravel.model.dto.LoginDto;
import online.talkandtravel.model.dto.user.UserDtoWithAvatarAndPassword;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.util.constants.ApiPathConstants;
import online.talkandtravel.util.mapper.UserMapper;
import org.springframework.http.ResponseEntity;
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
  private final AuthenticationService authService;
  private final UserMapper mapper;

  @Operation(description = "Register a user.")
  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@RequestBody UserDtoWithAvatarAndPassword dto) {
    log.info("register - {}", dto);
    var user = mapper.mapToUserWithPassword(dto);
    var authResponse = authService.register(user);
    return ResponseEntity.ok(authResponse);
  }

    @Operation(
            description = "Log in a user."
    )
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginDto loginDto) {
        log.info("Login - {}", loginDto);
        return authService.login(loginDto.getUserEmail(), loginDto.getPassword());
    }
}
