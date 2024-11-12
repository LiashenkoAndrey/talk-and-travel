package online.talkandtravel.model.dto.user;

import online.talkandtravel.model.dto.avatar.AvatarDto;

/**
 * Data Transfer Object (DTO) for representing a short summary of user information.
 */

public record UserDtoShort(
    Long id,
    String userName,
    String userEmail,
    AvatarDto avatar
) {

}
