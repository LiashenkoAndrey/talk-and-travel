package online.talkandtravel.model.dto.user;

/**
 * Data transfer object representing a user's name.
 *
 * @param id The unique identifier of the user.
 * @param userName The name of the user.
 */
public record UserNameDto(
    Long id,
    String userName,
    String avatarUrl
) {

}
