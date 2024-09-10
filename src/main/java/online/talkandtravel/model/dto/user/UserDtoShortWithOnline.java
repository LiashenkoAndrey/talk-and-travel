package online.talkandtravel.model.dto.user;

public record UserDtoShortWithOnline(
    Long id,
    String userName,
    String userEmail,
    Boolean isOnline
) {
}
