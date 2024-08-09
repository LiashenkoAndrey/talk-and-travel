package online.talkandtravel.controller.http;

import jakarta.validation.Valid;
import online.talkandtravel.model.dto.AuthResponse;
import online.talkandtravel.model.dto.LoginDto;
import online.talkandtravel.model.dto.user.UserDtoWithAvatarAndPassword;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.util.constants.ApiPathConstants;
import online.talkandtravel.util.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
/**
 * Controller class responsible for handling HTTP requests related to user authentication.
 * This includes user registration and login operations.
 *
 * <ul>
 *   <li>{@code register} - Handles user registration by mapping the DTO to a user model and delegating the registration process to the authentication service.</li>
 *   <li>{@code login} - Authenticates a user based on their email and password, returning an authentication response upon success.</li>
 * </ul>
 */


@RestController
@RequestMapping(ApiPathConstants.API_BASE_PATH + "/authentication")
@RequiredArgsConstructor
@Log4j2
public class AuthenticationController {
    private final AuthenticationService authService;
    private final UserMapper mapper;

    @Operation(
            description = "Register a user."
    )
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserDtoWithAvatarAndPassword dto) throws IOException {
        log.info("register - {}", dto);
        var user = mapper.mapToModel(dto);
        var authResponse = authService.register(user);
        return ResponseEntity.ok(authResponse);
    }

    @Operation(
            description = "Log in a user."
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDto loginDto) {
        log.info("Login - {}", loginDto);
        var authResponse = authService.login(loginDto.getUserEmail(), loginDto.getPassword());
        return ResponseEntity.ok(authResponse);
    }
}
