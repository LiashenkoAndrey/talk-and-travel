package online.talkandtravel.service;

import java.io.IOException;
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
 *   <li>{@link #save(Avatar)} - Saves the provided {@link Avatar} object to the database. This
 *       method is used to persist changes to avatar data.
 *   <li>{@link #findByUserId(Long)} - Retrieves the {@link Avatar} associated with the specified
 *       user ID. Returns the avatar if found, or null if no avatar is associated with the given
 *       user ID.
 *   <li>{@link #createDefaultAvatar(String)} - Creates a default avatar for a new user with the
 *       given username. This method is used to generate and return a default avatar image,
 *       typically used when a user has not uploaded a custom avatar. This method may throw an
 *       {@link IOException} if there are issues during image creation.
 *   <li>{@link #uploadAvatar(MultipartFile, Long)} - Handles the upload of a new avatar image file
 *       for a user with the specified user ID. The uploaded file is processed and saved as the
 *       user's avatar. Returns the newly created {@link Avatar} object.
 * </ul>
 */
public interface AvatarService {

  Avatar save(Avatar avatar);

  Avatar findByUserId(Long userId);

  Avatar createDefaultAvatar(String username);

  Long uploadAvatar(MultipartFile imageFile, Long userId);
}
