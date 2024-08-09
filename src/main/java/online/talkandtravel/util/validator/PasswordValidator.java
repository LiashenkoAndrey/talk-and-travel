package online.talkandtravel.util.validator;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Validator for checking the validity of passwords based on predefined criteria.
 *
 * <p>This class validates passwords to ensure they meet specific security requirements. The
 * validation is performed using a regular expression pattern that enforces the following rules:
 *
 * <ul>
 *   <li>Contains at least one letter (either uppercase or lowercase).
 *   <li>Contains at least one digit.
 *   <li>Contains at least one special character (non-word character).
 *   <li>Password length must be between 8 and 16 characters inclusive.
 * </ul>
 */
@Component
public class PasswordValidator {
  private static final String VALID_PASSWORD_PATTERN =
      "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[\\W])[\\w\\W]{8,16}$";

  public boolean isValid(String password) {
    return Pattern.matches(VALID_PASSWORD_PATTERN, password);
  }
}
