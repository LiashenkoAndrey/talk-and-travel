package online.talkandtravel.model.dto.message;

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

public record SendMessageRequest(String content, Long chatId, Long senderId, Long repliedMessageId) {}
