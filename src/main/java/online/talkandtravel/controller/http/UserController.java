package online.talkandtravel.controller.http;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.UserFacade;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoShort;
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.constants.ApiPathConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
@RequestMapping(ApiPathConstants.API_BASE_PATH )
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final UserFacade userFacade;

  @Operation(description = "Replace all user related data to placeholders")
  @DeleteMapping("/user")
  public ResponseEntity<?> deleteUser() {
    userFacade.deleteUser();
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PutMapping("/users")
  public UpdateUserResponse update(@RequestBody @Valid UpdateUserRequest dto) {
    return userService.update(dto);
  }

  @GetMapping("/v2/users")
  public List<UserDtoShort> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/users/{userId}")
  public UserDtoBasic findById(@PathVariable @Positive Long userId) {
    return userService.findById(userId);
  }

  @Operation(description = "Check if email exists.")
  @GetMapping("/users/exists-by-email")
  public Boolean existsByEmail(@RequestParam @Email String email) {
    return userService.existsByEmail(email);
  }
}