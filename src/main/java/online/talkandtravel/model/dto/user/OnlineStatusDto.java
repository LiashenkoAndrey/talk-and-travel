package online.talkandtravel.model.dto.user;

import java.time.LocalDateTime;

public record OnlineStatusDto(
    Long userId,
    Boolean isOnline,
    LocalDateTime lastSeenOn
) {

  public OnlineStatusDto(Long userId, Boolean isOnline) {
    this(userId, isOnline, null);
  }

}
