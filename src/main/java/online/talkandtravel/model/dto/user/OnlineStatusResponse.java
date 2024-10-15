package online.talkandtravel.model.dto.user;

import java.time.ZonedDateTime;

public record OnlineStatusResponse(
    Boolean isOnline,
    ZonedDateTime lastSeenOn
) {

}
