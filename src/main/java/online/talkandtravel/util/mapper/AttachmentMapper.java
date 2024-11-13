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

  protected static final String THUMBNAIL_FOLDER = IMAGE_ATTACHMENT_THUMBNAIL_X256_FOLDER_PATTERN;
  protected static final String ORIGINAL_FOLDER = IMAGE_ATTACHMENT_ORIGINAL_FOLDER_PATTERN;

  @Mapping(target = "thumbnailImageUrl", expression = "java(generateAvatarUrl(image, THUMBNAIL_FOLDER, chatId))")
  @Mapping(target = "originalImageUrl", expression = "java(generateAvatarUrl(image, ORIGINAL_FOLDER, chatId))")
  @Mapping(target = "type", expression = "java(image.getDiscriminatorValue())")
  public abstract ImageAttachmentDto toImageAttachmentDto(Image image, Long chatId);

  public String generateAvatarUrl(Image image, String avatarS3Folder, Long chatId) {
    return attachmentService.generateImageUrl(image, avatarS3Folder.formatted(chatId));
  }
}
