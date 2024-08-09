package online.talkandtravel.exception.auth;

import online.talkandtravel.exception.model.ApiException;

/**
 * Custom exception class for handling authentication errors.
 *
 * <p>This exception is used to signal issues related to authentication within the application. It
 * extends {@link ApiException} to provide a consistent exception hierarchy and leverage existing
 * exception handling mechanisms.
 *
 * <p>This exception can be thrown in scenarios such as:
 *
 * <ul>
 *   <li>Invalid credentials provided during authentication attempts.
 *   <li>Failed authentication due to expired or revoked tokens.
 *   <li>Other authentication-related issues that require custom handling and messaging.
 * </ul>
 */
public class AuthenticationException extends ApiException {



  public AuthenticationException(String message) {
    super(message);
  }
}
