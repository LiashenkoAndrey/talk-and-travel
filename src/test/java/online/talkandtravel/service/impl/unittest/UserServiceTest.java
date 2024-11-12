package online.talkandtravel.service.impl.unittest;

import static online.talkandtravel.testdata.UserTestData.getAlice;
import static online.talkandtravel.testdata.UserTestData.getAliceDtoBasic;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.auth.UserRegistrationDataNotFound;
import online.talkandtravel.exception.user.UserAlreadyExistsException;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.auth.SocialRegisterRequest;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.impl.UserServiceImpl;
import online.talkandtravel.util.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@Log4j2
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private AuthenticationService authenticationService;
  @Mock private UserMapper userMapper;
  @Mock private RedisTemplate<String, RegisterRequest> redisTemplate;
  @Mock private ValueOperations<String, RegisterRequest> valueOperations;

  @InjectMocks private UserServiceImpl underTest;

  private User alice;

  private static final String aliceEmail =  getAlice().getUserEmail();

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
    alice = getAlice();
  }

  private static final Long USER_REGISTER_DATA_EXPIRING_TIME_IN_MIN = 30L; // Example expiration time
  private static final String USER_REGISTER_DATA_REDIS_KEY_PATTERN = "register-user-data:%s";


  @Test
  void testUpdateLastLoggedOnToNow() {
    User user = new User();
    user.setId(1L);

    ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
    when(userRepository.save(any(User.class))).thenReturn(user);

    underTest.updateLastLoggedOnToNow(user);

    verify(userRepository).save(user);
    assertEquals(now.getHour(), user.getLastLoggedOn().getHour());
    assertEquals(now.getMinute(), user.getLastLoggedOn().getMinute());
    assertEquals(now.getSecond(), user.getLastLoggedOn().getSecond());
  }

  @Test
  void testMapToUserDtoBasic() {
    UserDtoBasic userDtoBasic = getAliceDtoBasic();

    when(userMapper.toUserDtoBasic(alice)).thenReturn(userDtoBasic);

    UserDtoBasic result = underTest.mapToUserDtoBasic(alice);

    verify(userMapper).toUserDtoBasic(alice);
    assertEquals(userDtoBasic, result);
  }


  @Test
  void testSaveNewUser_RegisterRequest() {
    RegisterRequest request = new RegisterRequest("username", "email@example.com", "password");
    UserDtoBasic userDtoBasic = getAliceDtoBasic();

    when(userMapper.registerRequestToUser(request)).thenReturn(alice);
    when(userRepository.save(alice)).thenReturn(alice);
    when(userMapper.toUserDtoBasic(alice)).thenReturn(userDtoBasic);

    UserDtoBasic result = underTest.saveNewUser(request);

    verify(userMapper).registerRequestToUser(request);
    verify(userRepository).save(alice);
    verify(userMapper).toUserDtoBasic(alice);
    assertEquals(userDtoBasic, result);
  }

  @Test
  void testSaveNewUser_SocialRegisterRequest() {
    SocialRegisterRequest request = new SocialRegisterRequest("username", "email@example.com");
    UserDtoBasic userDtoBasic = getAliceDtoBasic();

    when(userMapper.registerRequestToUser(request)).thenReturn(alice);
    when(userRepository.save(alice)).thenReturn(alice);
    when(userMapper.toUserDtoBasic(alice)).thenReturn(userDtoBasic);

    UserDtoBasic result = underTest.saveNewUser(request);

    verify(userMapper).registerRequestToUser(request);
    verify(userRepository).save(alice);
    verify(userMapper).toUserDtoBasic(alice);
    assertEquals(userDtoBasic, result);
  }

  @Nested
  class UpdateUserPassword {

    @Test
    void testUpdateUserPassword() {
      User user = new User();
      user.setId(1L);
      String rawPassword = "plainPassword";
      String encodedPassword = "encodedPassword";

      when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

      underTest.updateUserPassword(user, rawPassword);
      assertEquals(encodedPassword, user.getPassword());

      verify(passwordEncoder).encode(rawPassword);
      verify(userRepository).save(user);
    }
  }

  @Nested
  class SaveUserRegisterDataToTempStorage{

    @BeforeEach
    void setUp() {
      when(redisTemplate.opsForValue()).thenReturn(valueOperations);
      underTest.USER_REGISTER_DATA_EXPIRING_TIME_IN_MIN = USER_REGISTER_DATA_EXPIRING_TIME_IN_MIN;
    }

    @Test
    void testSaveUserRegisterDataToTempStorage() {
      String token = "sampleToken";
      String password = "plainPassword";
      String encodedPassword = "encodedPassword";
      RegisterRequest request = new RegisterRequest("testUser", "test@example.com", password);

      when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

      underTest.saveUserRegisterDataToTempStorage(token, request);

      String expectedKey = String.format(USER_REGISTER_DATA_REDIS_KEY_PATTERN, token);
      RegisterRequest expectedData = new RegisterRequest(request.userName(), request.userEmail(), encodedPassword);
      Duration expireTime = Duration.ofMinutes(USER_REGISTER_DATA_EXPIRING_TIME_IN_MIN);

      verify(valueOperations).set(expectedKey, expectedData, expireTime);
      verify(passwordEncoder).encode(password);
    }
  }

  @Nested
  class GetUserRegisterDataFromTempStorage {

    @BeforeEach
    void setUp() {
      when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }


    private final String token = "sampleToken";

    @Test
    void testGetUserRegisterDataFromTempStorage_DataExists() {
      RegisterRequest expectedData = new RegisterRequest("testUser", "test@example.com", "encodedPassword");

      String expectedKey = String.format(USER_REGISTER_DATA_REDIS_KEY_PATTERN, token);
      when(valueOperations.getAndDelete(expectedKey)).thenReturn(expectedData);

      RegisterRequest result = underTest.getUserRegisterDataFromTempStorage(token);

      assertEquals(expectedData, result);
      verify(valueOperations).getAndDelete(expectedKey);
    }

    @Test
    void testGetUserRegisterDataFromTempStorage_DataNotFound() {
      String expectedKey = String.format(USER_REGISTER_DATA_REDIS_KEY_PATTERN, token);

      when(valueOperations.getAndDelete(expectedKey)).thenReturn(null);

      assertThrows(UserRegistrationDataNotFound.class, () -> underTest.getUserRegisterDataFromTempStorage(token));
      verify(valueOperations).getAndDelete(expectedKey);
    }
  }

  @Nested
  class Save {

    @Test
    void save_shouldEncodePassword_whenUserIsCorrect() {
      String encodedPassword = "encodedPassword";
      alice.setPassword(encodedPassword);
      when(passwordEncoder.encode(getAlice().getPassword())).thenReturn(encodedPassword);
      when(userRepository.save(alice)).thenReturn(alice);

      underTest.save(getAlice());

      verify(passwordEncoder).encode(getAlice().getPassword());
      verify(userRepository).save(alice);
      verify(userMapper).toUserDtoBasic(alice);
    }
  }

  @Nested
  class Update {

    @Test
    void update_shouldUpdateUser() {
      String newName = "new name", newAbout = "new about";
      UpdateUserRequest request = new UpdateUserRequest(newName, alice.getUserEmail(), newAbout);
      UpdateUserResponse expectedResult = new UpdateUserResponse(newName, alice.getUserEmail(), newAbout);

      when(authenticationService.getAuthenticatedUser()).thenReturn(alice);
      doNothing().when(userMapper).updateUser(request, alice);
      alice.setUserName(newName);
      alice.setAbout(newAbout);
      when(userRepository.save(alice)).thenReturn(alice);
      when(userMapper.toUpdateUserResponse(alice)).thenReturn(expectedResult);

      UpdateUserResponse result = underTest.update(request);

      assertNotNull(result);
      assertEquals(expectedResult, result);

      verify(userMapper).updateUser(request, alice);
      verify(userMapper).toUpdateUserResponse(alice);
      verify(userRepository).save(alice);
    }
  }

  @Nested
  class UserByEmail {
    @Test
    void findUserByEmail_whenUserExists() {
      when(userRepository.findByUserEmail(aliceEmail)).thenReturn(
          Optional.ofNullable(alice));

      Optional<User> userOptional = underTest.findUserByEmail(aliceEmail);

      assertTrue(userOptional.isPresent());
      assertEquals(alice, userOptional.get());

      verify(userRepository).findByUserEmail(aliceEmail);
    }

    @Test
    void testFindUserByEmail_UserDoesNotExist() {
      when(userRepository.findByUserEmail(aliceEmail)).thenReturn(Optional.empty());

      Optional<User> result = underTest.findUserByEmail(aliceEmail);

      assertFalse(result.isPresent());
      verify(userRepository).findByUserEmail(aliceEmail);
    }
  }

  @Nested
  class FindById {
    @Test
    void testFindById_UserExists() {
      UserDtoBasic mockDto = getAliceDtoBasic();
      when(userRepository.findById(alice.getId())).thenReturn(Optional.of(alice));
      when(userMapper.toUserDtoBasic(alice)).thenReturn(mockDto);

      UserDtoBasic result = underTest.findById(alice.getId());

      assertEquals(mockDto, result);
      verify(userRepository).findById(alice.getId());
      verify(userMapper).toUserDtoBasic(alice);
    }

    @Test
    void testFindById_UserNotFound() {
      when(userRepository.findById(alice.getId())).thenReturn(Optional.empty());

      assertThrows(UserNotFoundException.class, () -> underTest.findById(alice.getId()));
      verify(userRepository).findById(alice.getId());
    }
  }

  @Nested
  class ExistsByEmail {
    @Test
    void testExistsByEmail_UserExists() {
      when(userRepository.findByUserEmail(aliceEmail)).thenReturn(Optional.of(new User()));

      boolean result = underTest.existsByEmail(aliceEmail);

      assertTrue(result);
      verify(userRepository).findByUserEmail(aliceEmail);
    }

    @Test
    void testExistsByEmail_UserDoesNotExist() {
      when(userRepository.findByUserEmail(aliceEmail)).thenReturn(Optional.empty());

      boolean result = underTest.existsByEmail(aliceEmail);

      assertFalse(result);
      verify(userRepository).findByUserEmail(aliceEmail);
    }

  }

  @Nested
  class CheckForDuplicateEmail {
    @Test
    void testCheckForDuplicateEmail_EmailDoesNotExist() {
      when(userRepository.existsByUserEmail(aliceEmail)).thenReturn(false);

      assertDoesNotThrow(() -> underTest.checkForDuplicateEmail(aliceEmail));
      verify(userRepository).existsByUserEmail(aliceEmail);
    }

    @Test
    void testCheckForDuplicateEmail_EmailExists() {
      when(userRepository.existsByUserEmail(aliceEmail)).thenReturn(true);

      assertThrows(UserAlreadyExistsException.class, () -> underTest.checkForDuplicateEmail(aliceEmail));
      verify(userRepository).existsByUserEmail(aliceEmail);
    }

  }
}