package online.talkandtravel.model.dto.user;

import online.talkandtravel.model.dto.avatar.AvatarDto;

/**
 * Data transfer object representing a user's name.
 *
 * @param id The unique identifier of the user.
 * @param userName The name of the user.
 */
public record UserNameDto(
    Long id,
    String userName,
    AvatarDto avatar
) {

}
