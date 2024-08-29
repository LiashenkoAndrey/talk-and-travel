package online.talkandtravel.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @Email String userEmail,
    @NotBlank String password
    ) {
}
