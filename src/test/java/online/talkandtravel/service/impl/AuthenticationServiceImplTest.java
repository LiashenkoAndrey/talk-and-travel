package online.talkandtravel.service.impl;

import online.talkandtravel.exception.RegistrationException;
import online.talkandtravel.model.dto.UserDtoShort;
import online.talkandtravel.model.entity.Avatar;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.dto.AuthResponse;
import online.talkandtravel.model.dto.UserDtoWithAvatarAndPassword;
import online.talkandtravel.service.AvatarService;
import online.talkandtravel.service.JwtService;
import online.talkandtravel.service.TokenService;
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.mapper.UserMapper;
import online.talkandtravel.util.validator.PasswordValidator;
import online.talkandtravel.util.validator.UserEmailValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {
    private static final String USER_PASSWORD = "!123456Aa";
    private static final String USER_NAME = "Bob";
    private static final String USER_EMAIL = "bob@mail.com";
    private static final String TEST_TOKEN = "test_token";
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;
    private static final Long USER_ID = 1L;
    @Mock
    private PasswordValidator passwordValidator;
    @Mock
    private UserEmailValidator emailValidator;
    @Mock
    private UserService userService;
    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TokenService tokenService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AvatarService avatarService;
    private User user;
    private UserDtoShort userDto;

    @BeforeEach
    void setUp() {
        userDto = creanteNewUserDtoShort();
        user = createNewUser();
    }

    @Test
    void register_shouldSaveUserWithCorrectCredentials() throws IOException {
        when(userService.save(any())).thenReturn(user);
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(Optional.empty());
        when(emailValidator.isValid(USER_EMAIL)).thenReturn(true);
        when(passwordValidator.isValid(USER_PASSWORD)).thenReturn(true);
        when(avatarService.createDefaultAvatar(USER_NAME)).thenReturn(new Avatar());
        when(userMapper.mapToShortDto(user)).thenReturn(userDto);

        UserDtoShort expected = creanteNewUserDtoShort();

        AuthResponse authResponse = authenticationService.register(user);
        UserDtoShort actual = authResponse.getUserDto();

        assertEquals(expected, actual);
    }

    @Test
    void register_shouldThrowRegistrationException_whenUserExists() {
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(emailValidator.isValid(USER_EMAIL)).thenReturn(true);
        when(passwordValidator.isValid(USER_PASSWORD)).thenReturn(true);

        assertThrows(RegistrationException.class,
                () -> authenticationService.register(user)
        );

        verify(userService, times(1)).findUserByEmail(USER_EMAIL);
    }

    @Test
    void register_shouldThrowRegistrationException_whenInvalidEmail() {
        assertThrows(RegistrationException.class,
                () -> authenticationService.register(user)
        );

        verify(emailValidator, times(1)).isValid(USER_EMAIL);
    }

    @Test
    void register_shouldThrowRegistrationException_whenInvalidPassword() {
        when(emailValidator.isValid(USER_EMAIL)).thenReturn(true);
        when(passwordValidator.isValid(USER_PASSWORD)).thenReturn(false);

        assertThrows(RegistrationException.class,
                () -> authenticationService.register(user)
        );

        verify(passwordValidator, times(1)).isValid(USER_PASSWORD);
    }

    @Test
    void register_shouldSaveTokenForNewUser() throws IOException {
        String expected = TEST_TOKEN;

        when(userService.save(any())).thenReturn(user);
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(Optional.empty());
        when(emailValidator.isValid(USER_EMAIL)).thenReturn(true);
        when(passwordValidator.isValid(USER_PASSWORD)).thenReturn(true);
        when(avatarService.createDefaultAvatar(USER_NAME)).thenReturn(new Avatar());
        when(avatarService.save(any())).thenReturn(new Avatar());
        when(jwtService.generateToken(user)).thenReturn(expected);

        AuthResponse authResponse = authenticationService.register(user);
        String actual = authResponse.getToken();

        assertEquals(expected, actual);

        verify(tokenService, times(1)).save(any());
    }

    @Test
    void login_shouldLoginUserWithCorrectCredentials() {
        when(userService.findUserByEmail(USER_EMAIL)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(USER_PASSWORD, USER_PASSWORD)).thenReturn(true);
        when(userMapper.mapToShortDto(user)).thenReturn(userDto);

        UserDtoShort expected = creanteNewUserDtoShort();

        AuthResponse authResponse = authenticationService.login(user.getUserEmail(), user.getPassword());
        UserDtoShort actual = authResponse.getUserDto();

        assertEquals(expected, actual);
    }

    private User createNewUser() {
        return User.builder()
                .id(USER_ID)
                .password(USER_PASSWORD)
                .userName(USER_NAME)
                .userEmail(USER_EMAIL)
                .build();
    }

    private UserDtoWithAvatarAndPassword creanteNewUserDto() {
        return UserDtoWithAvatarAndPassword.builder()
                .id(USER_ID)
                .userEmail(USER_EMAIL)
                .password(USER_PASSWORD)
                .userName(USER_NAME)
                .build();
    }

    private UserDtoShort creanteNewUserDtoShort() {
        return UserDtoShort.builder()
            .id(USER_ID)
            .userEmail(USER_EMAIL)
            .userName(USER_NAME)
            .build();
    }
}
