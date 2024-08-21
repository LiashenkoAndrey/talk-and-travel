package online.talkandtravel.model.dto;

import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoShort;

/**
 * Data Transfer Object (DTO) for authentication responses. This object is used to convey
 * authentication results which include:
 *
 * <ul>
 *   <li>{@code token} - The authentication token provided upon successful login or registration.
 *   <li>{@code userDto} - Shortened user information, encapsulated in {@link UserDtoShort},
 *       representing the authenticated user.
 * </ul>
 */
public record AuthResponse(
    String token,
    UserDtoBasic userDto
) {

}
