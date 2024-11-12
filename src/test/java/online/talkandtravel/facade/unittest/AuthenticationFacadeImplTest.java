package online.talkandtravel.facade.unittest;

import static online.talkandtravel.testdata.UserTestData.getAlice;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.user.UserAlreadyExistsException;
import online.talkandtravel.facade.impl.AuthenticationFacadeImpl;
import online.talkandtravel.model.dto.auth.AuthResponse;
import online.talkandtravel.model.dto.auth.LoginRequest;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.auth.RegistrationConfirmationRequest;
import online.talkandtravel.model.dto.avatar.AvatarDto;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.MailService;
import online.talkandtravel.service.TokenService;
import online.talkandtravel.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
  @Mock private AuthenticationService authenticationService;
  @Mock private MailService mailService;

  @InjectMocks AuthenticationFacadeImpl underTest;

  private static final String TEST_TOKEN = "test_token";

  private User alice;
  private RegisterRequest registerRequest;
  private UserDtoBasic userDtoBasic;

  @BeforeEach
  void setUp() {
    alice = getAlice();
    registerRequest = new RegisterRequest(alice.getUserName(), alice.getUserEmail(), alice.getPassword());
    userDtoBasic = new UserDtoBasic(1L, "testUser", "Test User", "test@example.com", new AvatarDto());
    SecurityContextHolder.clearContext();
  }

  @Nested
  class OnUserRegister {

    @Test
    void onUserRegister_shouldSaveToRedisAndSendEmail() {
      doNothing().when(userService).checkForDuplicateEmail(alice.getUserEmail());
      doNothing().when(userService).saveUserRegisterDataToTempStorage(anyString(), eq(registerRequest));
      doNothing().when(mailService).sendConfirmRegistrationMessage(eq(alice.getUserEmail()), anyString());

      underTest.onUserRegister(registerRequest);

      verify(userService).checkForDuplicateEmail(alice.getUserEmail());
      verify(userService).saveUserRegisterDataToTempStorage(anyString(), eq(registerRequest));
      verify(mailService).sendConfirmRegistrationMessage(eq(alice.getUserEmail()), anyString());
    }

    @Test
    void onUserRegister_shouldThrowException_whenUserExists() {
      doThrow(UserAlreadyExistsException.class)
          .when(userService)
          .checkForDuplicateEmail(alice.getUserEmail());

      assertThrows(UserAlreadyExistsException.class, () -> underTest.onUserRegister(registerRequest));
    }
  }

  @Nested
  class ConfirmRegistration {

    private static final RegistrationConfirmationRequest confirmationRequest = new RegistrationConfirmationRequest(TEST_TOKEN);

    @Test
    void confirmRegistration_shouldDeleteDataFromRedisAndSaveUser() {
      when(userService.getUserRegisterDataFromTempStorage(TEST_TOKEN)).thenReturn(registerRequest);
      when(userService.saveNewUser(registerRequest)).thenReturn(userDtoBasic);
      when(tokenService.generateToken(anyLong())).thenReturn(TEST_TOKEN);

      AuthResponse authResponse = underTest.confirmRegistration(confirmationRequest);

      assertNotNull(authResponse);
      assertEquals(TEST_TOKEN, authResponse.token());
      assertEquals(userDtoBasic, authResponse.userDto());

      verify(userService).getUserRegisterDataFromTempStorage(TEST_TOKEN);
      verify(userService).saveNewUser(registerRequest);
    }

  }

  @Test
  void login_shouldLoginUserWithCorrectCredentials() {
    LoginRequest loginRequest = new LoginRequest(alice.getUserEmail(), alice.getPassword());
    UserDtoBasic expected = new UserDtoBasic(alice.getId(), alice.getUserName(), alice.getUserEmail(), alice.getAbout(), new AvatarDto());

    when(authenticationService.checkUserCredentials(alice.getUserEmail(), alice.getPassword())).thenReturn(alice);
    doNothing().when(userService).updateLastLoggedOnToNow(alice);
    when(userService.mapToUserDtoBasic(alice)).thenReturn(expected);

    AuthResponse authResponse = underTest.login(loginRequest);
    UserDtoBasic actual = authResponse.userDto();

    assertEquals(expected, actual);

    verify(authenticationService).checkUserCredentials(alice.getUserEmail(), alice.getPassword());
    verify(userService).mapToUserDtoBasic(alice);
    verify(userService).updateLastLoggedOnToNow(alice);
  }

  @Test
  void testAuthenticateUser() {
    CustomUserDetails details = new CustomUserDetails(alice);

    when(tokenService.extractId(TEST_TOKEN)).thenReturn(alice.getId());
    when(userService.getUserDetails(alice.getId())).thenReturn(details);

    underTest.authenticateUser(TEST_TOKEN, request);

    verify(tokenService).extractId(TEST_TOKEN);
    verify(userService).getUserDetails(alice.getId());
  }
}
