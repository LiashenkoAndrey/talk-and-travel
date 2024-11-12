package online.talkandtravel.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
    @NotBlank
    @Size(
        min = 2,
        max = 16,
        message = "The username must be at least 2 and no more than 16 characters long")
    String userName,

    @NotBlank
    @Email(message = "Invalid email format")
    String userEmail,

    @NotNull
    @Pattern(
        regexp = "^.{8,30}$",
        message = "Password must be between 8 and 30 characters")
    String password
) {

}
