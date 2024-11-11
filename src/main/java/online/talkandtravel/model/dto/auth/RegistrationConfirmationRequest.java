package online.talkandtravel.model.dto.auth;

import jakarta.validation.constraints.NotNull;

public record RegistrationConfirmationRequest(
    @NotNull String token
) {

}
