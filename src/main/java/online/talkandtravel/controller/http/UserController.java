package online.talkandtravel.controller.http;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import online.talkandtravel.model.dto.user.UserDtoWithAvatarAndPassword;
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
@RequestMapping(ApiPathConstants.API_BASE_PATH + "/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserMapper userMapper;

  @Operation(description = "Update a user.")
  @PutMapping()
  public ResponseEntity<UserDtoWithAvatarAndPassword> update(
      @RequestBody UserDtoWithAvatarAndPassword dto) {
    var user = userMapper.mapToModel(dto);
    var updatedUser = userService.update(user);
    var userDto = userMapper.toUserDtoWithAvatarAndPassword(updatedUser);
    return ResponseEntity.ok().body(userDto);
  }

  @Operation(description = "Get a user by ID.")
  @GetMapping("/{userId}")
  public ResponseEntity<UserDtoWithAvatarAndPassword> findById(@PathVariable Long userId) {
    var user = userService.findById(userId);
    var userDto = userMapper.toUserDtoWithAvatarAndPassword(user);
    return ResponseEntity.ok().body(userDto);
  }

  @Operation(description = "Check if email exists.")
  @GetMapping("/exists-by-email/{email}")
  public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
    boolean exists = userService.existsByEmail(email);
    return ResponseEntity.ok().body(exists);
  }
}
