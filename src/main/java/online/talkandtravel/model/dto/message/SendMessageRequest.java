package online.talkandtravel.model.dto.message;


public record SendMessageRequest(String content, Long chatId, Long senderId, Long repliedMessageId) {}
