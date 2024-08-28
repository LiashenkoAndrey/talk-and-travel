package online.talkandtravel.service.impl.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@Log4j2
class AuthenticationServiceImplTest {

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
