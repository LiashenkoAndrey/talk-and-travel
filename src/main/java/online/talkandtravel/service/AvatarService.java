package online.talkandtravel.service;

import java.io.IOException;
import online.talkandtravel.model.dto.avatar.AvatarDto;
import online.talkandtravel.model.entity.Avatar;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for managing user avatars.
 *
 * <p>This service handles operations related to user avatars, including saving, retrieving,
 * creating default avatars, and uploading new ones.
 *
 * <p>Methods:
 *
 * <ul>
 *   <li>{@link #findByUserId(Long)} - Retrieves the {@link Avatar} associated with the specified
 *       user ID. Returns the avatar if found, or null if no avatar is associated with the given
 *       user ID.
 *       {@link IOException} if there are issues during image creation.
 *   <li>{@link #saveOrUpdateUserAvatar(MultipartFile)} - Handles the upload of a new avatar image file
 *       for a user with the specified user ID. The uploaded file is processed and saved as the
 *       user's avatar. Returns the newly created {@link Avatar} object.
 * </ul>
 */
public interface AvatarService {

  void validateFile(MultipartFile file);

  Avatar findByUserId(Long userId);

  String generateImageUrl(Avatar avatar, String avatarS3Folder);

  AvatarDto saveOrUpdateUserAvatar(byte[] image, String folder);
}
