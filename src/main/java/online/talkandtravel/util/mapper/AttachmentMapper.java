package online.talkandtravel.util.mapper;

import static online.talkandtravel.util.constants.S3Constants.IMAGE_ATTACHMENT_ORIGINAL_FOLDER_PATTERN;
import static online.talkandtravel.util.constants.S3Constants.IMAGE_ATTACHMENT_THUMBNAIL_X256_FOLDER_PATTERN;

import online.talkandtravel.config.MapperConfig;
import online.talkandtravel.model.dto.attachment.ImageAttachmentDto;
import online.talkandtravel.model.entity.attachment.Image;
import online.talkandtravel.service.AttachmentService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(
    config = MapperConfig.class
)
public abstract class AttachmentMapper {

  @Autowired
  @Lazy
  private AttachmentService attachmentService;

  protected static final String THUMBNAIL_FOLDER = IMAGE_ATTACHMENT_THUMBNAIL_X256_FOLDER_PATTERN.formatted(1);
  protected static final String ORIGINAL_FOLDER = IMAGE_ATTACHMENT_ORIGINAL_FOLDER_PATTERN.formatted(1);

  @Mapping(target = "thumbnailImageUrl", expression = "java(generateAvatarUrl(image, THUMBNAIL_FOLDER))")
  @Mapping(target = "originalImageUrl", expression = "java(generateAvatarUrl(image, ORIGINAL_FOLDER))")
  public abstract ImageAttachmentDto toImageAttachmentDto(Image image);

  public String generateAvatarUrl(Image image, String avatarS3Folder) {
    return attachmentService.generateImageUrl(image, avatarS3Folder);
  }
}
