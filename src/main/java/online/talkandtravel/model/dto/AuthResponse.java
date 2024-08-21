package online.talkandtravel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
  private String token;
  private UserDtoBasic userDto;
}
