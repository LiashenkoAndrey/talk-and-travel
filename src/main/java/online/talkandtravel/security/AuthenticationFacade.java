package online.talkandtravel.security;

import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.user.UserNotAuthenticatedException;
import online.talkandtravel.model.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * The purpose of this class is to abstract and simplify the interaction
 * with {@link SecurityContextHolder}, making the retrieval of the authenticated user easier and
 * decoupling the static access to security context from the rest of the codebase
 * To fully leverage the Spring dependency injection and be able to retrieve the authenticated user
 * everywhere, not just in @RestController beans
 */
@Component
@Log4j2
public class AuthenticationFacade implements IAuthenticationFacade {

  /**
   * gets User entity that stored in spring security
   */
  @Override
  public User getAuthenticatedUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object principal = authentication.getPrincipal();
    return getUserFromPrincipal(principal);
  }

  /**
   * In a spring security if a user not auth, principal equals 'anonymousUser' string
   * This method checks if principal is not {@link CustomUserDetails} class then throw an exception
   */
  private User getUserFromPrincipal(Object principal) {
    ifPrincipalIsStringThrowException(principal);
    verifyPrincipal(principal);
    CustomUserDetails userDetails = (CustomUserDetails) principal;
    return userDetails.getUser();
  }

  /**
   * This method checks if a principal is not a {@link CustomUserDetails} class
   * then throw an exception
   */
  private void verifyPrincipal(Object principal) {
    if (!(principal instanceof CustomUserDetails)) {
      log.error("principal is not an instance of a online.talkandtravel.security.CustomUserDetails");
      throw new UserNotAuthenticatedException("Unexpected exception!");
    }
  }

  /**
   * If a principal is a string then throw an exception
   */
  private void ifPrincipalIsStringThrowException(Object principal) {
    if (principal instanceof String) {
      log.error("principal is string");
      throw new UserNotAuthenticatedException();
    }
  }
}