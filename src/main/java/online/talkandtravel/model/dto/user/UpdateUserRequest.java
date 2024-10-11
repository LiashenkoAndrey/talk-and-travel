package online.talkandtravel.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateUserRequest(
    @NotBlank String userName,
    @Email String userEmail,
    @NotNull String about
) {

}
