package online.talkandtravel.util.validator;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;

/**
 * Validator for checking the validity of email addresses.
 *
 * <p>This class uses Apache Commons Validator to validate email addresses according to standard
 * email format rules. It ensures that the provided email address adheres to commonly accepted
 * patterns for email addresses.
 *
 * <p>The email validation is performed using the {@link
 * org.apache.commons.validator.routines.EmailValidator} class, which provides a robust mechanism
 * for validating email formats.
 */
@Component
public class UserEmailValidator {
  public boolean isValid(String email) {
    EmailValidator emailValidator = EmailValidator.getInstance();
    return emailValidator.isValid(email);
  }
}
