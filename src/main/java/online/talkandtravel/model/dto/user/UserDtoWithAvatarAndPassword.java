package online.talkandtravel.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import online.talkandtravel.model.dto.avatar.AvatarFileDto;

/**
 * Data Transfer Object (DTO) for representing detailed user information including avatar and
 * password.
 *
 * <ul>
 *   <li>{@code id} - Unique identifier for the user.
 *   <li>{@code userName} - Username of the user.
 *   <li>{@code userEmail} - Email address of the user.
 *   <li>{@code password} - Password associated with the user account.
 *   <li>{@code avatar} - Avatar information for the user, encapsulated in {@link AvatarFileDto}.
 *   <li>{@code about} - Brief description or additional information about the user.
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoWithAvatarAndPassword {
  private Long id;
  private String userName;
  private String userEmail;
  private String password;
  private AvatarFileDto avatar;
  private String about;
}
