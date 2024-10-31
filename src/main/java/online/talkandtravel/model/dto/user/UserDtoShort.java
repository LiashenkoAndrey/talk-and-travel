package online.talkandtravel.model.dto.user;

/**
 * Data Transfer Object (DTO) for representing a short summary of user information.
 */

public record UserDtoShort(
    Long id,
    String userName,
    String userEmail,
    String avatarUrl
) {

}
