package online.talkandtravel.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(
    @NotBlank String userName,
    @Email String userEmail,
    @NotBlank String userPassword
) {}
