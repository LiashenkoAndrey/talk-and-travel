package online.talkandtravel.facade.impl;

import static online.talkandtravel.util.FilesUtils.bytesToMegabytes;
import static online.talkandtravel.util.constants.AttachmentConstants.SUPPORTED_IMAGE_ATTACHMENT_FORMATS;
import static online.talkandtravel.util.constants.AvatarDimensionsConstants.X256;
import static online.talkandtravel.util.constants.S3Constants.IMAGE_ATTACHMENT_ORIGINAL_FOLDER_PATTERN;
import static online.talkandtravel.util.constants.S3Constants.IMAGE_ATTACHMENT_THUMBNAIL_X256_FOLDER_PATTERN;

import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.file.FileSizeExceededException;
import online.talkandtravel.exception.file.UnsupportedImageFormatException;
import online.talkandtravel.facade.MessageFacade;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.message.SendMessageWithAttachmentRequest;
import online.talkandtravel.model.entity.attachment.AttachmentType;
import online.talkandtravel.model.entity.attachment.Image;
import online.talkandtravel.service.AttachmentService;
import online.talkandtravel.service.ImageService;
import online.talkandtravel.service.MessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Log4j2
@RequiredArgsConstructor
public class MessageFacadeImpl implements MessageFacade {

  @Value("${attachments.images.maxSize}")
  private Long IMAGES_MAX_SIZE;


  private final MessageService messageService;
  private final AttachmentService attachmentService;
  private final ImageService imageService;

  @Override
  public MessageDto saveMessageWithAttachment(SendMessageWithAttachmentRequest file) {
    log.info("save message with attachment of type: {}", file.attachmentType());
    switch (file.attachmentType()) {
      case IMAGE -> {
        return saveMessageWithImage(file);
      }
      case VIDEO -> {
        return saveMessageWithVideo(file);
      }
      default -> throw new RuntimeException(
          "Specified attachment type in invalid, it must be: " + Arrays.toString(
              AttachmentType.values()));
    }
  }

  private MessageDto saveMessageWithImage(SendMessageWithAttachmentRequest request) {
    MultipartFile file = request.file();
    log.info("Save image attachment: {}", file.getOriginalFilename());
    validateImageFile(file);
    String key = UUID.randomUUID().toString();
    Image image = Image.builder()
        .id(key)
        .fileName(file.getOriginalFilename())
        .build();

    MessageDto messageDto = messageService.saveMessageWithImage(request, image);

    saveToS3(file, request.chatId(), key);
    return messageDto;
  }

  private void saveToS3(MultipartFile file, Long chatId, String key) {
    log.info("save image to s3: {}", file.getOriginalFilename());
    byte[] thumbnail = imageService.generateThumbnail(file, X256);

    attachmentService.saveImage(thumbnail, IMAGE_ATTACHMENT_THUMBNAIL_X256_FOLDER_PATTERN.formatted(chatId), file.getContentType(), key);
    attachmentService.saveImage(getBytes(file), IMAGE_ATTACHMENT_ORIGINAL_FOLDER_PATTERN.formatted(chatId), file.getContentType(), key);
  }

  private byte[] getBytes(MultipartFile file) {
    try {
      return file.getBytes();
    } catch (Exception e) {
      log.info("Error when get bytes");
      throw new RuntimeException(e);
    }
  }

  private void validateImageFile(MultipartFile file) {
    validateImageSize(file.getSize());
    validateImageFileFormat(file.getContentType());
  }

  private void validateImageFileFormat(String contentType) {
    String type = MediaType.valueOf(contentType).getSubtype();
    log.info("Image type: {}", type);
    if (!Arrays.asList(SUPPORTED_IMAGE_ATTACHMENT_FORMATS).contains(type)) {
      throw new UnsupportedImageFormatException(type, SUPPORTED_IMAGE_ATTACHMENT_FORMATS);
    }
  }

  private void validateImageSize(Long size) {
    if (bytesToMegabytes(size) > IMAGES_MAX_SIZE) {
      throw new FileSizeExceededException(size.intValue());
    }
  }

  private MessageDto saveMessageWithVideo(SendMessageWithAttachmentRequest request) {
    return null;
  }
}
