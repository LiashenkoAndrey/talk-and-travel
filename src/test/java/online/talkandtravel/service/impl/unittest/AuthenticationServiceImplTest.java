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
import online.talkandtravel.model.entity.Role;
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

  @InjectMocks AuthenticationServiceImpl authenticationService;

  private static final String
      USER_PASSWORD = "!123456Aa",
  USER_NAME = "Bob",
  USER_EMAIL = "bob@mail.com",
  USER_ABOUT = "about me",
  TEST_TOKEN = "test_token";

  private static final Long USER_ID = 1L;
  private User user;
  private UserDtoBasic userDto;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
    userDto = creanteNewUserDtoBasic();
    user = createNewUser();
  }
//
//  @Test
//  void register_shouldSaveUserWithCorrectCredentials() {
//    when(userService.save(any())).thenReturn(user);
//    when(userService.findUserByEmail(USER_EMAIL)).thenReturn(Optional.empty());
//    when(emailValidator.isValid(USER_EMAIL)).thenReturn(true);
//    when(passwordValidator.isValid(USER_PASSWORD)).thenReturn(true);
//    when(userMapper.mapToBasicDto(user)).thenReturn(userDto);
//
//    UserDtoBasic expected = creanteNewUserDtoBasic();
//
//    AuthResponse authResponse = authenticationService.register(user);
//    UserDtoBasic actual = authResponse.userDto();
//
//    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
//  }
//
//  @Test
//  void register_shouldThrowRegistrationException_whenUserExists() {
//    when(userService.findUserByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
//    when(emailValidator.isValid(USER_EMAIL)).thenReturn(true);
//    when(passwordValidator.isValid(USER_PASSWORD)).thenReturn(true);
//
//    assertThrows(RegistrationException.class, () -> authenticationService.register(user));
//
//    verify(userService, times(1)).findUserByEmail(USER_EMAIL);
//  }
//
//  @Test
//  void register_shouldThrowRegistrationException_whenInvalidEmail() {
//    assertThrows(RegistrationException.class, () -> authenticationService.register(user));
//
//    verify(emailValidator, times(1)).isValid(USER_EMAIL);
//  }
//
//  @Test
//  void register_shouldThrowRegistrationException_whenInvalidPassword() {
//    when(emailValidator.isValid(USER_EMAIL)).thenReturn(true);
//    when(passwordValidator.isValid(USER_PASSWORD)).thenReturn(false);
//
//    assertThrows(RegistrationException.class, () -> authenticationService.register(user));
//
//    verify(passwordValidator, times(1)).isValid(USER_PASSWORD);
//  }
//
//  @Test
//  void register_shouldSaveTokenForNewUser() {
//    String expected = TEST_TOKEN;
//
//    when(userService.save(any())).thenReturn(user);
//    when(userService.findUserByEmail(USER_EMAIL)).thenReturn(Optional.empty());
//    when(emailValidator.isValid(USER_EMAIL)).thenReturn(true);
//    when(passwordValidator.isValid(USER_PASSWORD)).thenReturn(true);
//    when(tokenService.generateToken(user)).thenReturn(expected);
//
//    AuthResponse authResponse = authenticationService.register(user);
//    String actual = authResponse.token();
//
//    assertEquals(expected, actual);
//
//    verify(tokenService, times(1)).save(any());
//  }
//
//  @Test
//  void login_shouldLoginUserWithCorrectCredentials() {
//    when(userService.findUserByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
//    when(passwordEncoder.matches(USER_PASSWORD, USER_PASSWORD)).thenReturn(true);
//    when(userMapper.mapToBasicDto(user)).thenReturn(userDto);
//
//    UserDtoBasic expected = creanteNewUserDtoBasic();
//    log.info(expected);
//    AuthResponse authResponse =
//        authenticationService.login(user.getUserEmail(), user.getPassword());
//    UserDtoBasic actual = authResponse.userDto();
//
//    assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
//  }

  private User createNewUser() {
    return User.builder()
        .id(USER_ID)
        .password(USER_PASSWORD)
        .userName(USER_NAME)
        .userEmail(USER_EMAIL)
        .build();
  }

  private UserDtoBasic creanteNewUserDtoBasic() {
    return new UserDtoBasic(USER_ID, USER_NAME, USER_EMAIL, USER_ABOUT);
  }

  @Test
  void testGetAuthenticatedUser() {
    CustomUserDetails userDetails = mock(CustomUserDetails.class);
    User user = mock(User.class);
    when(userDetails.getUser()).thenReturn(user);
    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    User authenticatedUser = authenticationService.getAuthenticatedUser();

    assertEquals(user, authenticatedUser);
  }

  @Test
  void testIsUserAuth_Authenticated() {
    Authentication authentication = mock(Authentication.class);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    boolean isAuthenticated = authenticationService.isUserAuthenticated();

    assertTrue(isAuthenticated);
  }

  @Test
  void testIsUserAuth_NotAuthenticated() {
    SecurityContextHolder.clearContext();
    boolean isAuthenticated = authenticationService.isUserAuthenticated();

    assertFalse(isAuthenticated);
  }
//
//  @Test
//  void testAuthenticateUser() {
//    String token = "mockToken";
//    Long userId = 1L;
//    String remoteAddress = "127.0.0.1";
//    User user1 = createNewUser();
//    user1.setRole(Role.USER);
//    CustomUserDetails details = new CustomUserDetails(user1);
//
//    when(tokenService.extractId(token)).thenReturn(userId);
//    when(userService.getUserDetails(userId)).thenReturn(details);
//    when(request.getRemoteAddr()).thenReturn(remoteAddress);
//
//    authenticationService.authenticateUser(token, request);
//
//
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//    assertNotNull(authentication);
//    verify(tokenService, times(1)).extractId(token);
//    verify(userService, times(1)).getUserDetails(userId);
//    verify(request, times(1)).getRemoteAddr();
//    assertEquals(remoteAddress ,request.getRemoteAddr());
//    assertEquals(details, authentication.getPrincipal());
//  }
}
