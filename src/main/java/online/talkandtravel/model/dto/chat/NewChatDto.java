package online.talkandtravel.model.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
/**
 * DTO for creating a new chat.
 * <p>
 * Contains fields for chat name, description, and country ID.
 * </p>
 * <ul>
 *   <li>{@code name} - The name of the chat. It cannot be blank.</li>
 *   <li>{@code description} - A brief description of the chat. It cannot be blank.</li>
 *   <li>{@code countryId} - The ID of the country associated with the chat. It cannot be null.</li>
 * </ul>
 */

public record NewChatDto(
    @NotBlank String name,
    @NotBlank String description,
    @NotNull String countryId) {
}
