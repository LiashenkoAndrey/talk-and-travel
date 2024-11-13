package online.talkandtravel.facade.impl;

import static online.talkandtravel.util.FilesUtils.bytesToMegabytes;
import static online.talkandtravel.util.FilesUtils.toFile;
import static online.talkandtravel.util.constants.ApiPathConstants.MESSAGES_SUBSCRIBE_PATH;
import static online.talkandtravel.util.constants.AttachmentConstants.SUPPORTED_IMAGE_ATTACHMENT_FORMATS;
import static online.talkandtravel.util.constants.AvatarDimensionsConstants.X256;
import static online.talkandtravel.util.constants.S3Constants.IMAGE_ATTACHMENT_ORIGINAL_FOLDER_PATTERN;
import static online.talkandtravel.util.constants.S3Constants.IMAGE_ATTACHMENT_THUMBNAIL_X256_FOLDER_PATTERN;

import java.util.Arrays;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.attachment.UnsupportedAttachmentTypeException;
import online.talkandtravel.exception.file.FileSizeExceededException;
import online.talkandtravel.exception.file.UnsupportedImageFormatException;
import online.talkandtravel.facade.MessageFacade;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.message.SendMessageWithAttachmentRequest;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.attachment.AttachmentType;
import online.talkandtravel.model.entity.attachment.Image;
import online.talkandtravel.service.AttachmentService;
import online.talkandtravel.service.ImageService;
import online.talkandtravel.service.MessageService;
import online.talkandtravel.util.FileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
@EnableAsync
public class MessageFacadeImpl implements MessageFacade {

  @Value("${attachments.images.maxSize}")
  private Long IMAGES_MAX_SIZE;

  private final SimpMessagingTemplate messagingTemplate;
  private final MessageService messageService;
  private final AttachmentService attachmentService;
  private final ImageService imageService;

  @Override
  @Async
  public void saveMessageWithAttachment(SendMessageWithAttachmentRequest request, FileDto fileDto, User user) {
    log.info("save message with attachment of type: {}", request.attachmentType());
    if (request.attachmentType().equals(AttachmentType.IMAGE)) {
      MessageDto messageDto = saveMessageWithImage(request, toFile(request.file()), user);
      messagingTemplate.convertAndSend(MESSAGES_SUBSCRIBE_PATH.formatted(messageDto.chatId()), messageDto);
    }
  }

  @Override
  public void validateAttachmentType(AttachmentType attachmentType) {
    if (!Arrays.asList(AttachmentType.values()).contains(attachmentType)) {
      throw new UnsupportedAttachmentTypeException(attachmentType);
    }
  }

  public MessageDto saveMessageWithImage(SendMessageWithAttachmentRequest request, FileDto file, User user) {
      log.info("Save image attachment: {}", file.filename());
      validateImageFile(file);
      String key = UUID.randomUUID().toString();
      Image image = Image.builder()
          .id(key)
          .fileName(file.filename())
          .build();

      MessageDto messageDto = messageService.saveMessageWithImage(request, image, user);

      saveToS3(file, request.chatId(), key);
      return messageDto;
  }

  private void saveToS3(FileDto file, Long chatId, String key) {
    log.info("save image to s3: {}", file.filename());
    byte[] thumbnail = imageService.generateThumbnail(file.fileBytes(), file.contentType(), X256);

    attachmentService.saveImage(thumbnail, IMAGE_ATTACHMENT_THUMBNAIL_X256_FOLDER_PATTERN.formatted(chatId), file.contentType(), key);
    attachmentService.saveImage(file.fileBytes(), IMAGE_ATTACHMENT_ORIGINAL_FOLDER_PATTERN.formatted(chatId), file.contentType(), key);
  }

  private void validateImageFile(FileDto file) {
    validateImageSize(file.size());
    validateImageFileFormat(file.contentType());
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
}
