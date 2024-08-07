package online.talkandtravel.model.dto.chat;

import java.time.LocalDateTime;
import java.util.List;
import online.talkandtravel.model.dto.event.EventDtoBasic;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.ChatType;

public record ChatDto(
    Long id,
    String name,
    String description,
    ChatType chatType,
    LocalDateTime creationDate,
    List<UserDtoBasic> users,
    List<MessageDtoBasic> messages,
    List<EventDtoBasic> events) {}
