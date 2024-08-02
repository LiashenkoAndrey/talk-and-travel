package online.talkandtravel.util.validator;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {
    private static final String VALID_PASSWORD_PATTERN
            = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[\\W])[\\w\\W]{8,16}$";

    public boolean isValid(String password) {
        return Pattern.matches(VALID_PASSWORD_PATTERN, password);
    }
}
