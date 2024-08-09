package online.talkandtravel.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for user login requests. This object captures the necessary
 * information for authentication:
 *
 * <ul>
 *   <li>{@code password} - The password for user authentication, required for login.
 *   <li>{@code userEmail} - The email address associated with the user account, required for login.
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {
  @NotNull private String password;
  @NotNull private String userEmail;
}
