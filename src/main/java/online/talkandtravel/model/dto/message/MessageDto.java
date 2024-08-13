package online.talkandtravel.model.dto.message;

import java.time.LocalDateTime;
import online.talkandtravel.model.dto.user.UserNameDto;

/**
 * Data transfer object representing a message.
 *
 * @param id The unique identifier of the message.
 * @param content The content of the message.
 * @param creationDate The date and time when the message was created.
 * @param user The user who sent the message.
 * @param chatId The ID of the chat where the message was sent.
 * @param repliedMessageId The ID of the message being replied to, if applicable.
 */
public record MessageDto(
    Long id,
    String content,
    LocalDateTime creationDate,
    UserNameDto user,
    Long chatId,
    Long repliedMessageId) {}
