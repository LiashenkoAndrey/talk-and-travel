package online.talkandtravel.facade.impl;

import static online.talkandtravel.util.constants.AvatarDimensionsConstants.X256;
import static online.talkandtravel.util.constants.AvatarDimensionsConstants.X50;
import static online.talkandtravel.util.constants.S3Constants.AVATAR_X256_FOLDER_NAME;
import static online.talkandtravel.util.constants.S3Constants.AVATAR_X50_FOLDER_NAME;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.facade.AvatarFacade;
import online.talkandtravel.model.dto.avatar.AvatarDto;
import online.talkandtravel.service.AvatarService;
import online.talkandtravel.service.ImageService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Log4j2
@RequiredArgsConstructor
public class AvatarFacadeImpl implements AvatarFacade {

  private final ImageService imageService;
  private final AvatarService avatarService;

  @Override
  public AvatarDto saveOrUpdateAvatar(MultipartFile file) {
    log.info("Save user avatar");
    avatarService.validateFile(file);
    byte[] thumbnailX50 = imageService.generateThumbnail(getBytes(file), file.getContentType(), X50);
    byte[] thumbnailX256 = imageService.generateThumbnail(getBytes(file), file.getContentType(), X256);

    avatarService.saveOrUpdateUserAvatar(thumbnailX50, AVATAR_X50_FOLDER_NAME);
    return avatarService.saveOrUpdateUserAvatar(thumbnailX256, AVATAR_X256_FOLDER_NAME);
  }

  private byte[] getBytes(MultipartFile file) {
    try {
      return file.getBytes();
    } catch (Exception e) {
      log.info("Error when get bytes");
      throw new RuntimeException(e);
    }
  }
}
