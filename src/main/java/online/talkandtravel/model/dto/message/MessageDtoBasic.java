package online.talkandtravel.model.dto.message;

import java.time.LocalDateTime;

public record MessageDtoBasic(
    Long id, String content, LocalDateTime creationDate, Long senderId, Long chatId, Long repliedMessageId) {}
