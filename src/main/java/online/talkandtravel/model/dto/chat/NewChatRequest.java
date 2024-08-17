package online.talkandtravel.model.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NewChatRequest(
    @NotBlank String name,
    @NotBlank String description,
    @NotNull String countryId) {
}
