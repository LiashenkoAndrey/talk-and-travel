package online.talkandtravel.util;

import java.util.Collection;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public record CustomAuthentication(CustomUserDetails userDetails) implements Authentication {

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public Object getDetails() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return this.userDetails;
  }

  @Override
  public boolean isAuthenticated() {
    return false;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

  }

  /**
   * Returns the name of this principal.
   *
   * @return the name of this principal.
   */
  @Override
  public String getName() {
    return null;
  }
}