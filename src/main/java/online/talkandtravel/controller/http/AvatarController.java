package online.talkandtravel.controller.http;

import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import online.talkandtravel.model.entity.Avatar;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.AvatarService;
import online.talkandtravel.util.constants.ApiPathConstants;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller class responsible for handling HTTP requests related to user avatars.
 *
 * <ul>
 *   <li>{@code getByUserId} - Retrieves the avatar for a user identified by their user ID,
 *       returning the avatar content as a byte array.
 *   <li>{@code uploadImage} - Handles the upload of a new avatar image for a user, updating the
 *       existing avatar or creating a new one.
 * </ul>
 */
@RestController
@RequestMapping(ApiPathConstants.API_BASE_PATH)
@RequiredArgsConstructor
public class AvatarController {
  private final AvatarService avatarService;

  @Operation(description = "Get Avatar by User ID.")
  @GetMapping({"/avatars/user/{userID}", "/v2/user/{userID}/avatar"})
  private ResponseEntity<byte[]> getByUserId(@PathVariable @Positive Long userID) {
    Avatar avatar = avatarService.findByUserId(userID);
    return ResponseEntity.ok()
        .contentType(MediaType.valueOf(IMAGE_PNG_VALUE))
        .body(avatar.getContent());
  }

  @Operation(description = "Update avatar.")
  @PostMapping("/user/{userID}")
  public Long uploadImage(@RequestParam("image") MultipartFile image, @PathVariable Long userID) {
    return avatarService.uploadAvatar(image, userID);
  }
}
