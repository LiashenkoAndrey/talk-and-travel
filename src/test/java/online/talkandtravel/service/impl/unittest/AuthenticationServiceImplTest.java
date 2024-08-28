package online.talkandtravel.service.impl.unittest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.auth.RegistrationException;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.TokenRepository;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.AvatarService;
import online.talkandtravel.service.TokenService;
import online.talkandtravel.service.UserService;
import online.talkandtravel.service.impl.AuthenticationServiceImpl;
import online.talkandtravel.util.mapper.UserMapper;
import online.talkandtravel.util.validator.PasswordValidator;
import online.talkandtravel.util.validator.UserEmailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@Log4j2
class AuthenticationServiceImplTest {
  @Mock private TokenService tokenService;
  @Mock private TokenRepository tokenRepository;
  @Mock private UserDetailsService userDetailsService;
  @Mock private HttpServletRequest request;
  @Mock private PasswordValidator passwordValidator;
  @Mock private UserEmailValidator emailValidator;
  @Mock private UserService userService;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private UserMapper userMapper;
  @Mock private AvatarService avatarService;
  @Mock private UserRepository userRepository;

  @InjectMocks AuthenticationServiceImpl underTest;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void testGetAuthenticatedUser() {
    CustomUserDetails userDetails = mock(CustomUserDetails.class);
    User user = mock(User.class);
    when(userDetails.getUser()).thenReturn(user);
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    User authenticatedUser = underTest.getAuthenticatedUser();

    assertEquals(user, authenticatedUser);
  }

  @Test
  void testIsUserAuth_Authenticated() {
    Authentication authentication = mock(Authentication.class);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    boolean isAuthenticated = underTest.isUserAuthenticated();

    assertTrue(isAuthenticated);
  }

  @Test
  void testIsUserAuth_NotAuthenticated() {
    SecurityContextHolder.clearContext();
    boolean isAuthenticated = underTest.isUserAuthenticated();

    assertFalse(isAuthenticated);
  }
}
