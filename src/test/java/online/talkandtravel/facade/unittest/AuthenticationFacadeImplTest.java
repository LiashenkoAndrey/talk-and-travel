package online.talkandtravel.facade.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.auth.RegistrationException;
import online.talkandtravel.facade.impl.AuthenticationFacadeImpl;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.dto.auth.LoginRequest;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.Role;
import online.talkandtravel.model.entity.Token;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.TokenService;
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
@Log4j2
class AuthenticationFacadeImplTest {
  @Mock private TokenService tokenService;
  @Mock private HttpServletRequest request;
  @Mock private UserService userService;
  @Mock private UserMapper userMapper;
  @Mock private AuthenticationService authenticationService;

  @InjectMocks AuthenticationFacadeImpl underTest;

  private static final String
      USER_PASSWORD = "!123456Aa",
      USER_NAME = "Bob",
      USER_EMAIL = "bob@mail.com",
      USER_ABOUT = "about me",
      TEST_TOKEN = "test_token";

  private static final Long USER_ID = 1L;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void register_shouldSaveUserWithCorrectCredentials() {
    User user1 = createNewUser();
    UserDtoBasic userDtoBasic = new UserDtoBasic(USER_ID, USER_NAME, USER_EMAIL, null);
    UserDtoBasic expected = new UserDtoBasic(USER_ID, USER_NAME, USER_EMAIL, null);
    RegisterRequest registerRequest = new RegisterRequest(USER_NAME, USER_EMAIL, USER_PASSWORD);
    doNothing().when(authenticationService).validateUserEmailAndPassword(USER_EMAIL, USER_PASSWORD);
    doNothing().when(authenticationService).checkForDuplicateEmail(USER_EMAIL);
    when(userMapper.registerRequestToUser(registerRequest)).thenReturn(user1);
    when(userService.save(user1)).thenReturn(userDtoBasic);
    stubbingSaveOrUpdateUserTokenMethod(user1);

    AuthResponse authResponse = underTest.register(registerRequest);
    assertEquals(expected, authResponse.userDto());

    verify(authenticationService, times(1)).validateUserEmailAndPassword(USER_EMAIL, USER_PASSWORD);
    verify(authenticationService, times(1)).checkForDuplicateEmail(USER_EMAIL);
    verify(userMapper, times(1)).registerRequestToUser(registerRequest);
    verify(userService, times(1)).save(user1);
    verifyStubbingSaveOrUpdateUserTokenMethod();

  }

  @Test
  void register_shouldThrowRegistrationException_whenUserExists() {
    RegisterRequest registerRequest = new RegisterRequest(USER_NAME, USER_EMAIL, USER_PASSWORD);

    doThrow(RegistrationException.class)
        .when(authenticationService)
        .checkForDuplicateEmail(USER_EMAIL);

    assertThrows(RegistrationException.class, () -> underTest.register(registerRequest));

    verify(authenticationService).validateUserEmailAndPassword(USER_EMAIL, USER_PASSWORD);
  }

  @Test
  void register_shouldThrowRegistrationException_whenInvalidEmailOrPassword() {
    RegisterRequest registerRequest = new RegisterRequest(USER_NAME, USER_EMAIL, USER_PASSWORD);

    doThrow(RegistrationException.class)
        .when(authenticationService)
        .validateUserEmailAndPassword(USER_EMAIL, USER_PASSWORD);

    assertThrows(RegistrationException.class, () -> underTest.register(registerRequest));

    verify(authenticationService).validateUserEmailAndPassword(USER_EMAIL, USER_PASSWORD);
  }

  @Test
  void login_shouldLoginUserWithCorrectCredentials() {
    LoginRequest loginRequest = new LoginRequest(USER_EMAIL, USER_PASSWORD);
    UserDtoBasic expected = creanteNewUserDtoBasic();
    User authenticatedUser = createNewUser();

    when(authenticationService.checkUserCredentials(USER_EMAIL, USER_PASSWORD))
        .thenReturn(authenticatedUser);
    stubbingSaveOrUpdateUserTokenMethod(authenticatedUser);
    when(userMapper.toUserDtoBasic(authenticatedUser)).thenReturn(expected);

    AuthResponse authResponse = underTest.login(loginRequest);
    UserDtoBasic actual = authResponse.userDto();

    assertEquals(expected, actual);
    verify(authenticationService).checkUserCredentials(USER_EMAIL, USER_PASSWORD);
    verifyStubbingSaveOrUpdateUserTokenMethod();
    verify(userMapper).toUserDtoBasic(authenticatedUser);
  }

  @Test
  void testAuthenticateUser() {
    Long userId = 1L;
    User user = createNewUser();
    user.setRole(Role.USER);
    CustomUserDetails details = new CustomUserDetails(user);

    when(tokenService.extractId(TEST_TOKEN)).thenReturn(userId);
    when(userService.getUserDetails(userId)).thenReturn(details);

    underTest.authenticateUser(TEST_TOKEN, request);

    verify(tokenService, times(1)).extractId(TEST_TOKEN);
    verify(userService, times(1)).getUserDetails(userId);
  }

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

  private void stubbingSaveOrUpdateUserTokenMethod(User user) {
    when(tokenService.generateToken(USER_ID)).thenReturn(TEST_TOKEN);
    when(userService.getReferenceById(USER_ID)).thenReturn(user);
    doNothing().when(tokenService).deleteUserToken(USER_ID);
    when(tokenService.save(any(Token.class))).thenReturn(null);
  }

  private void verifyStubbingSaveOrUpdateUserTokenMethod() {
    verify(userService, times(1)).getReferenceById(USER_ID);
    verify(tokenService, times(1)).generateToken(USER_ID);
    verify(tokenService, times(1)).deleteUserToken(USER_ID);
    verify(tokenService, times(1)).save(any(Token.class));
  }
}
