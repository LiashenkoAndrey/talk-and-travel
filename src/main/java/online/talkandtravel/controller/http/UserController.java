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
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.constants.ApiPathConstants;
import online.talkandtravel.util.mapper.UserMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  private final UserMapper userMapper;

  @PutMapping
  public UpdateUserResponse update(@RequestBody @Valid UpdateUserRequest dto) {
    log.info("update {}", dto);
    var user = userMapper.mapToUser(dto);
    log.info("mapped user {}", user);
    var updatedUser = userService.update(user);
    return userMapper.toUpdateUserResponse(updatedUser);
  }

  @GetMapping("/{userId}")
  public UserDtoBasic findById(@PathVariable @Positive Long userId) {
    var user = userService.findById(userId);
    return userMapper.toUserDtoBasic(user);
  }

  @Operation(description = "Check if email exists.")
  @GetMapping("/exists-by-email/{email}")
  public ResponseEntity<Boolean> existsByEmail(@PathVariable @Email String email) {
    boolean exists = userService.existsByEmail(email);
    return ResponseEntity.ok().body(exists);
  }
}
