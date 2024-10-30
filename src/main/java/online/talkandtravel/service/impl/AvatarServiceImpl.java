package online.talkandtravel.service.impl;

import static online.talkandtravel.util.constants.FileFormatConstants.SUPPORTED_FORMAT_AVATAR;
import static online.talkandtravel.util.constants.S3Constants.AVATARS_FOLDER_NAME;
import static online.talkandtravel.util.constants.S3Constants.S3_BUCKET_NAME;
import static online.talkandtravel.util.constants.S3Constants.S3_URL_PATTERN;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.avatar.UserAvatarNotFoundException;
import online.talkandtravel.exception.file.FileSizeExceededException;
import online.talkandtravel.exception.file.ImageProcessingException;
import online.talkandtravel.exception.file.UnsupportedImageFormatException;
import online.talkandtravel.model.entity.Avatar;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.AvatarRepository;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.AvatarService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Implementation of the {@link AvatarService} for managing user avatars.
 *
 * <p>This service handles the operations related to avatars
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

  @Value("${avatars.max-size-in-mb}")
  private int MAX_SIZE_AVATAR; // Size in MB

  private final AvatarRepository avatarRepository;
  private final AuthenticationService authenticationService;
  private final S3Client s3Client;

  @Transactional
  @Override
  public Avatar findByUserId(Long userId) {
    return getAvatar(userId);
  }

  @Override
  public String generateImageUrl(Avatar avatar) {
    return S3_URL_PATTERN.formatted(S3_BUCKET_NAME, avatar.getKey());
  }

  @Override
  @Transactional
  public Avatar saveOrUpdateUserAvatar(MultipartFile file) {
    try {
      User user = authenticationService.getAuthenticatedUser();
      log.info("Save or update user avatar for user: {}", user.getId());
      validateFile(file);
      Optional<Avatar> avatarOptional = avatarRepository.findByUserId(user.getId());
      return avatarOptional.map(avatar -> update(file, avatar))
          .orElseGet(() -> save(file));

    } catch (Exception e) {
      log.error("Exception when save or update avatar: "  + e.getMessage(), e);
      throw new ImageProcessingException("Failed to process avatar image " + e.getMessage());
    }
  }

  private Avatar save(MultipartFile file) {
    log.info("save avatar");
    UUID key = saveImageToS3(file, UUID.randomUUID());
    return saveAvatar(key);
  }

  private Avatar update(MultipartFile file, Avatar avatar)  {
    log.info("update avatar");
    saveImageToS3(file, avatar.getKey());
    return avatar;
  }

  private UUID saveImageToS3(MultipartFile file, UUID key)  {
    try {

      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(S3_BUCKET_NAME)
          .key(AVATARS_FOLDER_NAME + key.toString())
          .contentType(file.getContentType())
          .build();

      s3Client.putObject(putObjectRequest, RequestBody.fromBytes(extractImageData(file)));
      return key;
    } catch (Exception e) {
      log.error(e);
      throw new RuntimeException(e);
    }
   }

  private byte[] extractImageData(MultipartFile imageFile) {
    try {
      return imageFile.getBytes();
    } catch (IOException e) {
      log.error("Error getting bytes." + e.getMessage());
      throw new ImageProcessingException(
          "Error getting bytes." + e.getMessage());
    }
  }

  private Avatar saveAvatar(UUID key) {
    User user = authenticationService.getAuthenticatedUser();
    return avatarRepository.save(Avatar.builder()
        .user(user)
        .key(key)
        .build());
  }

  private void validateFile(MultipartFile imageFile) {
    validateImageFormat(imageFile.getOriginalFilename());
    validateImageSize(imageFile);
  }

  private void validateImageFormat(String originalFilename) {
    if (!isSupportedFormat(originalFilename)) {
      throw new UnsupportedImageFormatException();
    }
  }

  private void validateImageSize(MultipartFile imageFile) {
    if (bytesToMegabytes(imageFile.getSize()) > MAX_SIZE_AVATAR) {
      throw new FileSizeExceededException(MAX_SIZE_AVATAR);
    }
  }

  public static double bytesToMegabytes(long bytes) {
    return bytes / (1024.0 * 1024.0);
  }

  private boolean isSupportedFormat(String fileName) {
    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    return Arrays.asList(SUPPORTED_FORMAT_AVATAR).contains(fileExtension);
  }

  private Avatar getAvatar(Long userId) {
    return avatarRepository
        .findByUserId(userId)
        .orElseThrow(() -> new UserAvatarNotFoundException(userId));
  }
}
