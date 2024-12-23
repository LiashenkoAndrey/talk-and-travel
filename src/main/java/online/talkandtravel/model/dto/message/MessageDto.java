package online.talkandtravel.model.dto.message;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.ZonedDateTime;
import online.talkandtravel.model.dto.attachment.AttachmentDto;
import online.talkandtravel.model.dto.user.UserNameDto;
import online.talkandtravel.model.entity.MessageType;
import online.talkandtravel.util.CustomZonedDateTimeDeserializer;

/**
 * Data transfer object representing a message.
 *
 * @param id The unique identifier of the message.
 * @param content The content of the message.
 * @param creationDate The date and time when the message was created.
 * @param user The user who sent the message.
 * @param chatId The ID of the chat where the message was sent.
 * @param repliedMessage The ID of the message being replied to, if applicable.
 */
public record MessageDto(
    Long id,
    MessageType type,
    String content,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'")
    @JsonDeserialize(using = CustomZonedDateTimeDeserializer.class)
    ZonedDateTime creationDate,
    UserNameDto user,
    Long chatId,
    MessageDtoShort repliedMessage,
    AttachmentDto attachment) {

  public MessageDto(Long id, MessageType type, String content, ZonedDateTime creationDate,
      UserNameDto user, Long chatId, MessageDtoShort repliedMessage) {
    this(id, type, content, creationDate, user, chatId, repliedMessage, null);
  }

  public MessageDto(String content) {
    this(null, null, content, null, null, null, null);
  }
}
