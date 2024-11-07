package online.talkandtravel.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record RecoverPasswordRequest(
    @NotNull @Email String userEmail
) {

}
