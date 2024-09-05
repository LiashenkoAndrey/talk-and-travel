package online.talkandtravel.model.dto.message;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Data Transfer Object (DTO) for representing a request to send a message.
 *
 * <ul>
 *   <li>{@code content} - Content of the message to be sent.</li>
 *   <li>{@code chatId} - ID of the chat where the message will be sent.</li>
 *   <li>{@code senderId} - ID of the user sending the message.</li>
 *   <li>{@code repliedMessageId} - ID of the message being replied to, if applicable.</li>
 * </ul>
 */

public record SendMessageRequest(
        @NotNull String content,
        @NotNull @Positive Long chatId,
        Long repliedMessageId
) {}
