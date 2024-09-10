package online.talkandtravel.service.impl.unittest;

import static online.talkandtravel.util.UserUtils.USER_ABOUT;
import static online.talkandtravel.util.UserUtils.USER_EMAIL;
import static online.talkandtravel.util.UserUtils.USER_ID;
import static online.talkandtravel.util.UserUtils.USER_NAME;
import static online.talkandtravel.util.UserUtils.USER_PASSWORD;
import static online.talkandtravel.util.UserUtils.createDefaultUser;
import static online.talkandtravel.util.service.EventDestination.USER_STATUS_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserOnlineStatus;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.impl.UserServiceImpl;
import online.talkandtravel.util.mapper.UserMapper;
import online.talkandtravel.util.service.EventPublisherUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@Log4j2
class UserServiceImplTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AuthenticationService authenticationService;
  @Mock private UserMapper userMapper;

  @Mock private EventPublisherUtil publisherUtil;

  @Mock private RedisTemplate<String, String> redisTemplate;

  @InjectMocks private UserServiceImpl underTest;

  private User user;

  @BeforeEach
  void setUp() {
    user = createDefaultUser();
    SecurityContextHolder.clearContext();
  }

  @Test
  void updateUserOnlineStatus_shouldUpdate_whenStatusIfOnline() {
    UserOnlineStatus status = UserOnlineStatus.ONLINE;
    Duration duration = Duration.ofSeconds(1);
    ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);
    String key = USER_STATUS_KEY.formatted(USER_ID);

    when(publisherUtil.getUserOnlineStatusExpirationDuration()).thenReturn(duration);
    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    doNothing().when(valueOperations).set(key, status.isOnline().toString(), duration);

    underTest.updateUserOnlineStatus(status, USER_ID);

    verify(publisherUtil, times(1)).getUserOnlineStatusExpirationDuration();
    verify(redisTemplate, times(1)).opsForValue();
    verify(valueOperations, times(1)).set(key, status.isOnline().toString(), duration);
  }

  @Test
  void updateUserOnlineStatus_shouldUpdate_whenStatusIfOffline() {
    UserOnlineStatus status = UserOnlineStatus.OFFLINE;
    ValueOperations<String, String> valueOperations = Mockito.mock(ValueOperations.class);
    String key = USER_STATUS_KEY.formatted(USER_ID);

    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    doNothing().when(valueOperations).set(key, status.isOnline().toString());

    underTest.updateUserOnlineStatus(status, USER_ID);

    verify(redisTemplate, times(1)).opsForValue();
    verify(valueOperations, times(1)).set(key, status.isOnline().toString());
  }

  @Test
  void save_shouldEncodePassword_whenUserIsCorrect() {
    UserDtoBasic expected = new UserDtoBasic(USER_ID, USER_NAME, USER_EMAIL, USER_ABOUT);

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

  private static Stream<Arguments> updateUserOnlineStatusTestArgs() {
    return Stream.of(
        Arguments.of(UserOnlineStatus.ONLINE),
        Arguments.of(UserOnlineStatus.OFFLINE)
    );
  }
}