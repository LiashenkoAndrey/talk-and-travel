package online.talkandtravel.facade.impl;

import static online.talkandtravel.util.constants.ApiPathConstants.MESSAGES_SUBSCRIBE_PATH;
import static online.talkandtravel.util.constants.AvatarDimensionsConstants.X256;
import static online.talkandtravel.util.constants.S3Constants.IMAGE_ATTACHMENT_ORIGINAL_FOLDER_PATTERN;
import static online.talkandtravel.util.constants.S3Constants.IMAGE_ATTACHMENT_THUMBNAIL_X256_FOLDER_PATTERN;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
@EnableAsync
public class MessageFacadeImpl implements MessageFacade {

  private final SimpMessagingTemplate messagingTemplate;
  private final MessageService messageService;
  private final AttachmentService attachmentService;
  private final ImageService imageService;

  @Override
  @Async
  public void saveMessageWithAttachment(SendMessageWithAttachmentRequest request, FileDto fileDto,
      User user) {
    log.info("save message with attachment of type: {}", request.attachmentType());
    if (request.attachmentType().equals(AttachmentType.IMAGE)) {
      saveMessageWithImageAndNotifySubscribers(request, fileDto, user);
    }
  }

  private void saveMessageWithImageAndNotifySubscribers(SendMessageWithAttachmentRequest request,
      FileDto fileDto, User user) {
    MessageDto messageDto = saveMessageWithImage(request, fileDto, user);
    log.info("Attachment with name: {} for message with id: {}, chat id: {}, from user: {} saved successfully",
        fileDto.filename(), messageDto.id(), messageDto.chatId(), messageDto.user().id());
    notifySubscribers(messageDto);
  }

  private MessageDto saveMessageWithImage(SendMessageWithAttachmentRequest request, FileDto file,
      User user) {
    log.info("Save image attachment: {}", file.filename());
    String key = UUID.randomUUID().toString();
    MessageDto messageDto = saveToDb(request, file, key, user);
    saveToS3(file, request.chatId(), key);
    return messageDto;
  }

  private MessageDto saveToDb(SendMessageWithAttachmentRequest request, FileDto file, String key,
      User user) {
    Image image = Image.builder()
        .id(key)
        .fileName(file.filename())
        .build();

    return messageService.saveMessageWithImage(request, image, user);
  }

  private void saveToS3(FileDto file, Long chatId, String key) {
    log.info("save image to s3: {}", file.filename());
    byte[] thumbnail = imageService.generateThumbnail(file.fileBytes(), file.contentType(), X256);

    attachmentService.saveImage(thumbnail,
        IMAGE_ATTACHMENT_THUMBNAIL_X256_FOLDER_PATTERN.formatted(chatId), file.contentType(), key);
    attachmentService.saveImage(file.fileBytes(),
        IMAGE_ATTACHMENT_ORIGINAL_FOLDER_PATTERN.formatted(chatId), file.contentType(), key);
  }

  private void notifySubscribers(MessageDto messageDto) {
    messagingTemplate.convertAndSend(MESSAGES_SUBSCRIBE_PATH.formatted(messageDto.chatId()),
        messageDto);
  }
}
