package online.talkandtravel.service.impl.unittest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.impl.UserServiceImpl;
import online.talkandtravel.util.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@Log4j2
class UserServiceTest {

  @Mock private UserRepository repository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private UserMapper userMapper;
  @Mock private AuthenticationService authenticationService;

  @InjectMocks private UserServiceImpl underTest;

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
    underTest.setAuthenticationService(authenticationService);
  }

  private static final Long USER_ID = 1L;

  private static final String
      USER_PASSWORD = "!123456Aa",
      USER_NAME = "Bob",
      USER_EMAIL = "bob@mail.com";

  private User createNewUser() {
    return User.builder()
        .id(USER_ID)
        .password(USER_PASSWORD)
        .userName(USER_NAME)
        .userEmail(USER_EMAIL)
        .build();
  }

  @Test
  void update_shouldUpdateUser_whenCorrectRequestData() {
    String newName = "new name";
    String newAbout = "new about";
    UpdateUserRequest request = new UpdateUserRequest(newName, USER_EMAIL, newAbout);
    User savedUser = createNewUser();
    CustomUserDetails details = new CustomUserDetails(savedUser);
    Authentication authentication = new UsernamePasswordAuthenticationToken(details, null);
    SecurityContextHolder.getContext().setAuthentication(authentication);

    when(authenticationService.getAuthenticatedUser()).thenReturn(savedUser);
    doNothing().when(userMapper).updateUser(any(UpdateUserRequest.class), any(User.class));
    when(repository.save(savedUser)).thenReturn(savedUser);

    underTest.update(request);

    verify(authenticationService, times(1)).getAuthenticatedUser();
    verify(userMapper, times(1)).updateUser(any(UpdateUserRequest.class), any(User.class));
    verify(repository, times(1)).save(savedUser);
  }
}