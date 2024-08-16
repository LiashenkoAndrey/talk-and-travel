package online.talkandtravel.security;

import online.talkandtravel.model.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * The purpose of this interface is to abstract and simplify the interaction with
 * {@link SecurityContextHolder}, making the retrieval of the authenticated user easier and
 * decoupling the static access to security context from the rest of the codebase
 *
 * <ul>
 *   <li>{@link #getAuthenticatedUser} gets the authenticated user
 * </ul>
 */
public interface IAuthenticationFacade {

  User getAuthenticatedUser();
}
