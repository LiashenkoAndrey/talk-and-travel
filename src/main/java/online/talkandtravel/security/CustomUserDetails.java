package online.talkandtravel.security;

import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import online.talkandtravel.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Custom implementation of {@link UserDetails} for Spring Security.
 *
 * <p>This class provides user-specific details required for authentication and authorization:
 * <ul>
 *   <li>{@link #getAuthorities()} - Returns the user's authorities based on their role.</li>
 *   <li>{@link #getPassword()} - Retrieves the user's password.</li>
 *   <li>{@link #getUsername()} - Retrieves the user's email as the username.</li>
 *   <li>{@link #isAccountNonExpired()} - Indicates that the user's account is not expired.</li>
 *   <li>{@link #isAccountNonLocked()} - Indicates that the user's account is not locked.</li>
 *   <li>{@link #isCredentialsNonExpired()} - Indicates that the user's credentials are not expired.</li>
 *   <li>{@link #isEnabled()} - Indicates that the user is enabled.</li>
 * </ul>
 */

@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
