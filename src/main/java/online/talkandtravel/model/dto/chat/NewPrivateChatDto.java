package online.talkandtravel.model.dto.chat;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record NewPrivateChatDto(
        Long userId,
        @NotNull @Positive Long companionId
) {}
