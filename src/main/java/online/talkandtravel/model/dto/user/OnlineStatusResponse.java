package online.talkandtravel.model.dto.user;

import java.time.LocalDateTime;

public record OnlineStatusResponse (
    Boolean isOnline,
    LocalDateTime lastSeenOn
) { }
