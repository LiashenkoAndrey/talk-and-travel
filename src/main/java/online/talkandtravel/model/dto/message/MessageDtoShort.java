package online.talkandtravel.model.dto.message;

import online.talkandtravel.model.dto.attachment.AttachmentDto;
import online.talkandtravel.model.dto.user.UserNameDto;
import online.talkandtravel.model.entity.MessageType;

/**
 * Data transfer object representing a message.
 *
 * @param id The unique identifier of the message.
 * @param content The content of the message.
 * @param user The user who sent the message.
 */
public record MessageDtoShort(
    Long id,
    MessageType type,
    String content,
    UserNameDto user,
    AttachmentDto attachment) {
}
