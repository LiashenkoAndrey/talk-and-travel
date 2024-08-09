package online.talkandtravel.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDtoBasic {

  private Long id;
  private String userName;
  private String userEmail;
  private String about;
}
