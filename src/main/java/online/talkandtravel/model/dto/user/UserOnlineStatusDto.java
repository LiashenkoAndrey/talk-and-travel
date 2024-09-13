package online.talkandtravel.model.dto.user;

public record UserOnlineStatusDto(
    Long userId,
    Boolean isOnline
) {

}
