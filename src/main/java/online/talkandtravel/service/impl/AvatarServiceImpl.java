package online.talkandtravel.service.impl;

import static online.talkandtravel.util.constants.AvatarDimensionsConstants.X256;
import static online.talkandtravel.util.constants.AvatarDimensionsConstants.X50;
import static online.talkandtravel.util.constants.FileFormatConstants.SUPPORTED_FORMAT_AVATAR;
import static online.talkandtravel.util.constants.S3Constants.AVATAR_X256_FOLDER_NAME;
import static online.talkandtravel.util.constants.S3Constants.AVATAR_X50_FOLDER_NAME;
import static online.talkandtravel.util.constants.S3Constants.S3_URL_PATTERN;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.avatar.UserAvatarNotFoundException;
import online.talkandtravel.exception.file.FileSizeExceededException;
import online.talkandtravel.exception.file.UnsupportedImageFormatException;
import online.talkandtravel.model.dto.avatar.AvatarDto;
import online.talkandtravel.model.entity.Avatar;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.AvatarRepository;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.AvatarService;
import online.talkandtravel.util.mapper.AvatarMapper;
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
  private int MAX_SIZE_AVATAR_IN_MB;

  @Value("${aws.region}")
  private String AWS_REGION;

  @Value("${aws.s3.bucketName}")
  private String AWS_S3_BUCKET_NAME;

  @Value("${aws.s3.avatarsFolderName}")
  private String AWS_S3_AVATARS_FOLDER_NAME;

  private final AvatarRepository avatarRepository;
  private final AuthenticationService authenticationService;
  private final S3Client s3Client;
  private final AvatarMapper avatarMapper;


  @Transactional
  @Override
  public Avatar findByUserId(Long userId) {
    return getAvatar(userId);
  }

  @Override
  public String generateImageUrl(Avatar avatar, String avatarS3Folder) {
    String fullAvatarFolderPath = AWS_S3_AVATARS_FOLDER_NAME + avatarS3Folder;
    return S3_URL_PATTERN.formatted(AWS_S3_BUCKET_NAME, AWS_REGION, fullAvatarFolderPath,
        avatar.getKey());
  }

  @Override
  @Transactional
  public AvatarDto saveOrUpdateUserAvatar(byte[] image, String folder) {
    User user = authenticationService.getAuthenticatedUser();
    log.info("Save or update user avatar for user: {}", user.getId());
    Optional<Avatar> avatarOptional = avatarRepository.findByUserId(user.getId());

    Avatar avatar;
    if (avatarOptional.isPresent()) {
      avatar = avatarOptional.get();
      update(image, avatar, folder);

    } else {
      avatar = save(image, folder);
    }
    return avatarMapper.toAvatarDto(avatar);
  }

  @Override
  public void validateFile(MultipartFile imageFile) {
    validateImageFormat(imageFile.getOriginalFilename());
    validateImageSize(imageFile);
  }

  private Avatar save(byte[] image, String folder) {
    UUID key = UUID.randomUUID();
    saveImageToS3(image, AWS_S3_AVATARS_FOLDER_NAME + folder, key);
    return saveAvatar(key);
  }

  private void update(byte[] image, Avatar avatar, String folder) {
    saveImageToS3(image, AWS_S3_AVATARS_FOLDER_NAME + folder, avatar.getKey());
  }

  private void saveImageToS3(byte[] bytes, String avatarFolderName, UUID key) {
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(AWS_S3_BUCKET_NAME)
        .key(avatarFolderName + "/" + key.toString())
        .contentType("png")
        .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
  }

  private Avatar saveAvatar(UUID key) {
    User user = authenticationService.getAuthenticatedUser();
    return avatarRepository.save(Avatar.builder()
        .user(user)
        .key(key)
        .build());
  }

  private void validateImageFormat(String originalFilename) {
    if (!isSupportedFormat(originalFilename)) {
      throw new UnsupportedImageFormatException();
    }
  }

  private void validateImageSize(MultipartFile imageFile) {
    if (bytesToMegabytes(imageFile.getSize()) > MAX_SIZE_AVATAR_IN_MB) {
      throw new FileSizeExceededException(MAX_SIZE_AVATAR_IN_MB);
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
