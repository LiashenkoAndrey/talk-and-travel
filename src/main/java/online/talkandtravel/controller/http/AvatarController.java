package online.talkandtravel.controller.http;

import online.talkandtravel.model.entity.Avatar;
import online.talkandtravel.service.AvatarService;
import online.talkandtravel.util.constants.ApiPathConstants;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;


@RestController
@RequestMapping(ApiPathConstants.API_BASE_PATH + "/avatars")
@RequiredArgsConstructor
public class AvatarController {
    private final AvatarService avatarService;

    @Operation(
            description = "Get Avatar by User ID."
    )
    @GetMapping("/user/{userID}")
    private ResponseEntity<byte[]> getByUserId(@PathVariable Long userID) {
        var avatar = avatarService.findByUserId(userID);
        return getResponse(avatar);
    }

    @Operation(
            description = "Update avatar."
    )
    @PostMapping("/user/{userID}")
    public ResponseEntity<byte[]> uploadImage(@RequestParam("image") MultipartFile image,
                                         @PathVariable Long userID) {
        var avatar = avatarService.uploadAvatar(image, userID);
        return getResponse(avatar);
    }

    private ResponseEntity<byte[]> getResponse(Avatar avatar) {
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(IMAGE_PNG_VALUE))
                .body(avatar.getContent());
    }
}
