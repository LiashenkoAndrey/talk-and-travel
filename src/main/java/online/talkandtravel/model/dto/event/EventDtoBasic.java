package online.talkandtravel.model.dto.event;

import java.time.LocalDateTime;
import online.talkandtravel.model.entity.EventType;

public record EventDtoBasic(Long id, Long authorId, EventType eventType, LocalDateTime eventTime) {}
