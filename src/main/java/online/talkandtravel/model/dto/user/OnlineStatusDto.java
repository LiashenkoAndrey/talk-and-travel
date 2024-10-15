package online.talkandtravel.model.dto.user;

import java.time.ZonedDateTime;

public record OnlineStatusDto(
    Long userId,
    Boolean isOnline,
    ZonedDateTime lastSeenOn
) {

  public OnlineStatusDto(Long userId, Boolean isOnline) {
    this(userId, isOnline, null);
  }
}
