package online.talkandtravel.service.impl;

import java.io.IOException;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.avatar.UserAvatarNotFoundException;
import online.talkandtravel.exception.file.FileSizeExceededException;
import online.talkandtravel.exception.file.ImageProcessingException;
import online.talkandtravel.exception.file.UnsupportedFormatException;
import online.talkandtravel.model.entity.Avatar;
import online.talkandtravel.repository.AvatarRepository;
import online.talkandtravel.service.AvatarService;
import online.talkandtravel.service.ImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of the {@link AvatarService} for managing user avatars.
 *
 * <p>This service handles the following operations related to avatars:
 *
 * <ul>
 *   <li>{@link #save(Avatar)} - Saves an {@link Avatar} entity to the repository.
 *   <li>{@link #findByUserId(Long)} - Retrieves an avatar associated with a specific user ID.
 *   <li>{@link #createDefaultAvatar(String)} - Creates a default avatar for a user with the given
 *       username.
 *   <li>{@link #uploadAvatar(MultipartFile, Long)} - Uploads and saves a new avatar for a user from
 *       a multipart file.
 *   <li>{@link #getExistingAvatar(Long)} - Retrieves an existing avatar or throws an exception if
 *       not found.
 *   <li>{@link #validateImage(MultipartFile, String)} - Validates the format and size of the
 *       uploaded image file.
 *   <li>{@link #validateImageFormat(String)} - Checks if the file format is supported (JPEG, JPG,
 *       or PNG).
 *   <li>{@link #validateImageSize(MultipartFile)} - Ensures the file size does not exceed the
 *       maximum allowed size (4MB).
 *   <li>{@link #extractImageData(MultipartFile)} - Extracts image data from the multipart file.
 *   <li>{@link #isSupportedFormat(String)} - Determines if the file extension is among the
 *       supported formats.
 *   <li>{@link #createNewAvatar(byte[])} - Creates a new {@link Avatar} instance with the given
 *       image data.
 * </ul>
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {
  private static final String[] SUPPORTED_FORMAT_AVATAR = {"jpeg", "jpg", "png"};
  private static final int MAX_SIZE_AVATAR = 4 * 1024 * 1024; // Size in bytes (4MB)
  private final AvatarRepository repository;
  private final ImageService imageService;

  @Override
  public Avatar save(Avatar avatar) {
    return repository.save(avatar);
  }

  @Override
  @Transactional
  public Avatar findByUserId(Long userId) {
    return getExistingAvatar(userId);
  }

  @Override
  public Avatar createDefaultAvatar(String username) {
    log.info("createDefaultAvatar: username - {}", username);
    byte[] defaultAvatar = imageService.generateImage(username);
    return createNewAvatar(defaultAvatar);
  }

  @Override
  @Transactional
  public Avatar uploadAvatar(MultipartFile file, Long userId) {
    byte[] image = extractImageData(file);
    String filename = file.getOriginalFilename();
    validateImage(file, filename);
    var existingAvatar = getExistingAvatar(userId);
    existingAvatar.setContent(image);
    return repository.save(existingAvatar);
  }

  private Avatar getExistingAvatar(Long userId) {
    Avatar avatar =
        repository
            .findByUserId(userId)
            .orElseThrow(
                () -> new UserAvatarNotFoundException(userId));
    avatar.setContent(avatar.getContent());
    return avatar;
  }

  private void validateImage(MultipartFile imageFile, String originalFilename)
      throws UnsupportedFormatException, FileSizeExceededException {
    validateImageFormat(originalFilename);
    validateImageSize(imageFile);
  }

  private void validateImageFormat(String originalFilename) throws UnsupportedFormatException {
    if (!isSupportedFormat(originalFilename)) {
      throw new UnsupportedFormatException("Your photo must be in JPEG, JPG, or PNG.");
    }
  }

  private void validateImageSize(MultipartFile imageFile) throws FileSizeExceededException {
    if (imageFile.getSize() > MAX_SIZE_AVATAR) {
      throw new FileSizeExceededException("File size exceeds 4MB");
    }
  }

  private byte[] extractImageData(MultipartFile imageFile) {
    try {
      return imageFile.getBytes();
    } catch (IOException e) {
      throw new ImageProcessingException(
          "Error converting an image to an avatar. Please try again.");
    }
  }

  private boolean isSupportedFormat(String fileName) {
    var fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    return Arrays.asList(SUPPORTED_FORMAT_AVATAR).contains(fileExtension);
  }

  private Avatar createNewAvatar(byte[] standardAvatar) {
    return Avatar.builder().content(standardAvatar).build();
  }
}
