package online.talkandtravel.service.impl;

import online.talkandtravel.exception.AuthenticationException;
import online.talkandtravel.exception.RegistrationException;
import online.talkandtravel.exception.UserNotFoundException;
import online.talkandtravel.model.Role;
import online.talkandtravel.model.Token;
import online.talkandtravel.model.TokenType;
import online.talkandtravel.model.User;
import online.talkandtravel.model.dto.AuthResponse;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.AvatarService;
import online.talkandtravel.service.JwtService;
import online.talkandtravel.service.TokenService;
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.mapper.UserDtoMapper;
import online.talkandtravel.util.validator.PasswordValidator;
import online.talkandtravel.util.validator.UserEmailValidator;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthenticationServiceImpl implements AuthenticationService {
    private final PasswordValidator passwordValidator;
    private final UserEmailValidator emailValidator;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserDtoMapper userDtoMapper;
    private final AvatarService avatarService;

    @Override
    @Transactional
    public AuthResponse register(User user) throws IOException {
        var newUser = registerNewUser(user);
        String jwtToken = manageUserTokens(newUser);
        return createNewAuthResponse(jwtToken, newUser);
    }

    @Override
    @Transactional
    public AuthResponse login(String email, String password) {
        var authenticatedUser = authenticateUser(email, password);
        String jwtToken = manageUserTokens(authenticatedUser);
        return createNewAuthResponse(jwtToken, authenticatedUser);
    }

    private User authenticateUser(String email, String password) {
        Optional<User> userOptional = userService.findUserByEmail(email.toLowerCase());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            checkUserCredentials(password, user);
            return user;
        }
        log.info("User with email - {} not found", email);
        throw new UserNotFoundException("User with email - "+ email+" not found", "Bad credentials");
    }

    private User registerNewUser(User user) throws IOException {
        validateUserRegistrationData(user);
        var newUser = userService.save(createNewUser(user));
        generateStandardAvatar(newUser);
        return newUser;
    }

    private String manageUserTokens(User user) {
        String jwtToken = jwtService.generateToken(user);
        var token = createNewToken(jwtToken, user);
        revokeAllUserTokens(user);
        tokenService.deleteInvalidTokensByUserId(user.getId());
        tokenService.save(token);
        return jwtToken;
    }

    private void validateUserRegistrationData(User user) {
        String lowercaseEmail = user.getUserEmail().toLowerCase();
        user.setUserEmail(lowercaseEmail);
        validateEmailAndPassword(user);
        var userByEmail = userService.findUserByEmail(user.getUserEmail());
        checkForDuplicateEmail(userByEmail);
    }

    //todo: clarify an appointment of this method and create documentation
    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenService.findAllValidTokensByUserId(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenService.saveAll(validUserTokens);
    }

    private void generateStandardAvatar(User savedUser) throws IOException {
        log.info("generateStandardAvatar: savedUser - {}", savedUser);
        var avatar = avatarService.createDefaultAvatar(savedUser.getUserName());
        avatar.setUser(savedUser);
        avatarService.save(avatar);
    }

    /**
     * checks if passed password doesn't match password stored in database - throw an exception
     * @param password passed password
     * @param user user
     */
    private void checkUserCredentials(String password, User user) {
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new AuthenticationException("Incorrect username or password!!!");
        }
    }

    private void checkForDuplicateEmail(Optional<User> user) {
        if (user.isPresent()) {
            throw new RegistrationException("A user with this email already exists");
        }
    }

    private void validateEmailAndPassword(User user) {
        if (!emailValidator.isValid(user.getUserEmail())) {
            throw new RegistrationException("Invalid email address");
        }
        if (!passwordValidator.isValid(user.getPassword())) {
            throw new RegistrationException("Passwords must be 8 to 16 characters long and contain "
                    + "at least one letter, one digit, and one special character.");
        }
    }

    private AuthResponse createNewAuthResponse(String jwtToken, User user) {
        var userDto = userDtoMapper.mapToDto(user);
        return AuthResponse.builder()
                .token(jwtToken)
                .user(userDto)
                .build();
    }

    private Token createNewToken(String jwtToken, User savedUser) {
        return Token.builder()
                .user(savedUser)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
    }

    private User createNewUser(User user) {
        return User.builder()
                .userName(user.getUserName())
                .userEmail(user.getUserEmail().toLowerCase())
                .password(user.getPassword())
                .role(Role.USER)
                .build();
    }
}
