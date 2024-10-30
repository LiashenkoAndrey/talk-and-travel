package online.talkandtravel.service.impl.unittest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.impl.UserServiceImpl;
import online.talkandtravel.util.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@Log4j2
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AuthenticationService authenticationService;
  @Mock private UserMapper userMapper;

  @InjectMocks private UserServiceImpl underTest;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  private static final Long USER_ID = 1L;

  private static final String
      USER_PASSWORD = "!123456Aa",
      USER_NAME = "Bob",
      USER_EMAIL = "bob@mail.com",
      USER_ABOUT = "about me";

  @Test
  void save_shouldEncodePassword_whenUserIsCorrect() {
    User user = createDefaultUser();
    UserDtoBasic expected = new UserDtoBasic(USER_ID, USER_NAME, USER_EMAIL, USER_ABOUT, "url");

    when(passwordEncoder.encode(USER_PASSWORD)).thenReturn("encodedPassword");
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toUserDtoBasic(user)).thenReturn(expected);

    UserDtoBasic result = underTest.save(user);

    assertEquals(expected, result);
    verify(passwordEncoder, times(1)).encode(USER_PASSWORD);
    verify(userRepository, times(1)).save(user);
    verify(userMapper, times(1)).toUserDtoBasic(user);
  }

  @Test
  void save_shouldEncodePassword_whenUserIsNull() {
    assertThrows(NullPointerException.class, () -> underTest.save(null));
  }

  @Test
  void update_shouldUpdateUser_whenCorrectRequestData() {
    String newName = "new name", newAbout = "new about";
    UpdateUserRequest request = new UpdateUserRequest(newName, USER_EMAIL, newAbout);
    User existingUser = createDefaultUser();
    User user = createUpdatedUser(newName, newAbout);
    UpdateUserResponse expectedResult = new UpdateUserResponse(newName, USER_EMAIL, newAbout);

    when(authenticationService.getAuthenticatedUser()).thenReturn(existingUser);
    doNothing().when(userMapper).updateUser(request, existingUser);
    existingUser.setAbout(newAbout);
    existingUser.setUserName(newName);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.toUpdateUserResponse(user)).thenReturn(expectedResult);

    UpdateUserResponse result = underTest.update(request);

    assertEquals(expectedResult, result);
    verify(userMapper, times(1)).updateUser(request, existingUser);
    verify(userMapper, times(1)).toUpdateUserResponse(user);
    verify(userRepository, times(1)).save(user);
  }

  private User createUpdatedUser(String newName, String newAbout) {
    return User.builder()
        .id(USER_ID)
        .password(USER_PASSWORD)
        .userName(newName)
        .userEmail(USER_EMAIL)
        .about(newAbout)
        .build();
  }

  private User createDefaultUser() {
    return User.builder()
        .id(USER_ID)
        .password(USER_PASSWORD)
        .userName(USER_NAME)
        .userEmail(USER_EMAIL)
        .build();
  }
}