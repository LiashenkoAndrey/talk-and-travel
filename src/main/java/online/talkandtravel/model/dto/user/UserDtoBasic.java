package online.talkandtravel.model.dto.user;

/**
 * Data Transfer Object (DTO) for representing basic user information.
 *
 * <ul>
 *   <li>{@code id} - Unique identifier for the user.
 *   <li>{@code userName} - Username of the user.
 *   <li>{@code userEmail} - Email address of the user.
 *   <li>{@code about} - Brief description or additional information about the user.
 * </ul>
 */

public record UserDtoBasic(
    Long id,
    String userName,
    String userEmail,
    String about
) {

}