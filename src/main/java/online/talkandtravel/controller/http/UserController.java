package online.talkandtravel.controller.http;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.dto.user.UserOnlineStatusDto;
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.constants.ApiPathConstants;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.web.bind.annotation.*;

/**
 * Controller class responsible for handling HTTP requests related to user operations.
 *
 * <ul>
 *   <li>{@code update} - Updates user information based on the provided DTO and returns the updated
 *       user details.
 *   <li>{@code findById} - Retrieves user information by user ID and returns it in the form of a
 *       DTO.
 *   <li>{@code existsByEmail} - Checks if a user with the specified email address exists and
 *       returns a boolean result.
 * </ul>
 */
@RestController
@Log4j2
@RequestMapping(ApiPathConstants.API_BASE_PATH + "/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @PutMapping
  public UpdateUserResponse update(@RequestBody @Valid UpdateUserRequest dto) {
    return userService.update(dto);
  }

  @GetMapping("/{userId}")
  public UserDtoBasic findById(@PathVariable @Positive Long userId) {
    return userService.findById(userId);
  }

  @Operation(description = "Check if email exists.")
  @GetMapping("/exists-by-email")
  public Boolean existsByEmail(@RequestParam @Email String email) {
    return userService.existsByEmail(email);
  }
}
