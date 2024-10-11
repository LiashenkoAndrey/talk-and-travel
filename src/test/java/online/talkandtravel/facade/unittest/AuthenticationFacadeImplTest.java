package online.talkandtravel.facade.unittest;

import static online.talkandtravel.testdata.UserTestData.getAlice;
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
import online.talkandtravel.model.entity.Token;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.AuthenticationService;
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

  @InjectMocks AuthenticationFacadeImpl underTest;

  private static final String TEST_TOKEN = "test_token";

  private User alice;

  @BeforeEach
  void setUp() {
    alice = getAlice();
    SecurityContextHolder.clearContext();
  }

  @Nested
  class Register {

    private RegisterRequest registerRequest;
    private  UserDtoBasic aliseDtoBasic;

    @BeforeEach
    void setUp() {
      registerRequest = new RegisterRequest(alice.getUserName(), alice.getUserEmail(), alice.getPassword());
      aliseDtoBasic = new UserDtoBasic(alice.getId(), alice.getUserName(), alice.getUserEmail(), alice.getAbout());
    }

    @Test
    void register_shouldSaveUserWithCorrectCredentials() {
      doNothing().when(authenticationService).validateUserEmailAndPassword(alice.getUserEmail(), alice.getPassword());
      doNothing().when(authenticationService).checkForDuplicateEmail(alice.getUserEmail());
      when(userService.createAndSaveNewUser(registerRequest)).thenReturn(aliseDtoBasic);
      stubbingSaveOrUpdateUserTokenMethod(alice);

      AuthResponse authResponse = underTest.register(registerRequest);
      assertEquals(TEST_TOKEN, authResponse.token());
      assertEquals(aliseDtoBasic, authResponse.userDto());

      verify(authenticationService, times(1)).validateUserEmailAndPassword(alice.getUserEmail(),
          alice.getPassword());
      verify(authenticationService, times(1)).checkForDuplicateEmail(alice.getUserEmail());
      verify(userService, times(1)).createAndSaveNewUser(registerRequest);
      verifyStubbingSaveOrUpdateUserTokenMethod(alice);

    }

    @Test
    void register_shouldThrowRegistrationException_whenUserExists() {

      doThrow(RegistrationException.class)
          .when(authenticationService)
          .checkForDuplicateEmail(alice.getUserEmail());

      assertThrows(RegistrationException.class, () -> underTest.register(registerRequest));

      verify(authenticationService).validateUserEmailAndPassword(aliseDtoBasic.userEmail(),
          alice.getPassword());
    }

    @Test
    void register_shouldThrowRegistrationException_whenInvalidEmailOrPassword() {
      doThrow(RegistrationException.class)
          .when(authenticationService)
          .validateUserEmailAndPassword(aliseDtoBasic.userEmail(), alice.getPassword());

      assertThrows(RegistrationException.class, () -> underTest.register(registerRequest));

      verify(authenticationService).validateUserEmailAndPassword(aliseDtoBasic.userEmail(),
          alice.getPassword());
    }
  }

  @Test
  void login_shouldLoginUserWithCorrectCredentials() {
    LoginRequest loginRequest = new LoginRequest(alice.getUserEmail(), alice.getPassword());
    UserDtoBasic expected = new UserDtoBasic(alice.getId(), alice.getUserName(), alice.getUserEmail(), alice.getAbout());

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

  private void stubbingSaveOrUpdateUserTokenMethod(User user) {
    when(tokenService.generateToken(user.getId())).thenReturn(TEST_TOKEN);
    when(userService.getReferenceById(user.getId())).thenReturn(user);
    doNothing().when(tokenService).deleteUserToken(user.getId());
    when(tokenService.save(any(Token.class))).thenReturn(null);
  }

  private void verifyStubbingSaveOrUpdateUserTokenMethod(User user) {
    verify(userService).getReferenceById(user.getId());
    verify(tokenService).generateToken(user.getId());
    verify(tokenService).deleteUserToken(user.getId());
    verify(tokenService).save(any(Token.class));
  }
}
