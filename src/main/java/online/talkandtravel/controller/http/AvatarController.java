package online.talkandtravel.controller.http;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import online.talkandtravel.model.entity.Avatar;
import online.talkandtravel.service.AvatarService;
import online.talkandtravel.util.constants.ApiPathConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  @Operation(description = "Saved or updates an user avatar in Amazon S3 and database")
  @PostMapping("/v2/user/avatar")
  public ResponseEntity<String> uploadImage(@RequestParam("image") MultipartFile image) {
    Avatar avatar = avatarService.saveOrUpdateUserAvatar(image);
    String imageUrl = avatarService.generateImageUrl(avatar);
    return ResponseEntity.status(HttpStatus.CREATED).body(imageUrl);
  }
}
