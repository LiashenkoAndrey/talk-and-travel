package online.talkandtravel.security;

import lombok.RequiredArgsConstructor;
import online.talkandtravel.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of {@link UserDetailsService} for Spring Security.
 *
 * <p>This service loads user-specific data from the {@link UserRepository} based on the provided
 * email address. It converts the retrieved {@link User} entity into a {@link CustomUserDetails}
 * instance, which is used for authentication and authorization within Spring Security:
 *
 * <ul>
 *   <li>{@link #loadUserByUsername(String)} - Loads user details by email. Throws {@link
 *       UsernameNotFoundException} if the user is not found in the repository.
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
    return userRepository
        .findByUserEmail(userEmail)
        .map(CustomUserDetails::new)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }
}
