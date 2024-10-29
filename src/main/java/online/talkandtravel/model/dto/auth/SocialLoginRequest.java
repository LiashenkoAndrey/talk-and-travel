package online.talkandtravel.model.dto.auth;

import jakarta.validation.constraints.Email;

public record SocialLoginRequest (
    @Email String userEmail
) {

}
