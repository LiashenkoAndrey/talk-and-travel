package online.talkandtravel.exception.auth;

import online.talkandtravel.exception.model.ApiException;

/**
 * Custom exception class for handling registration errors.
 *
 * <p>This exception is used to signal issues related to user registration within the application.
 * It extends {@link ApiException} to integrate with the application's general exception hierarchy
 * and facilitate consistent exception handling.
 *
 * <p>This exception can be thrown in scenarios such as:
 *
 * <ul>
 *   <li>Invalid user data provided during registration attempts.
 *   <li>Failure to register a user due to duplicate email addresses or usernames.
 *   <li>Other registration-related issues that require specific handling and messaging.
 * </ul>
 */
public class RegistrationException extends ApiException {

  public RegistrationException(String message) {
    super(message);
  }
}
