package online.talkandtravel.model.dto.auth;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest (
    @NotNull String token,
    @NotNull @Size(max = 30, min = 8) String newPassword
) {

}
