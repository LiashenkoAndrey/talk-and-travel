package online.talkandtravel.model.dto.chat;

import jakarta.validation.constraints.NotNull;

public record NewPrivateChatDto(
   @NotNull Long userId,
   @NotNull Long companionId
) {}
