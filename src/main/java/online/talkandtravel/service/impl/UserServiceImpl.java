package online.talkandtravel.service.impl;

import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.AuthenticationException;
import online.talkandtravel.exception.RegistrationException;
import online.talkandtravel.model.User;
import online.talkandtravel.model.dto.UserIsTypingDTOResponse;
import online.talkandtravel.repository.UserRepo;
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.validator.UserEmailValidator;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {
    private final UserRepo repository;
    private final PasswordEncoder passwordEncoder;
    private final UserEmailValidator emailValidator;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Notifies all users which subscribed on /countries/{chatId}/texting-users that user started or
     * stopped typing
     * @param chatId        chat id
     * @param userId        user id
     * @param userName      user name
     * @param userIsTexting a boolean indicating whether the user has started or stopped typing
     */
    @Override
    public void notifyAllThatUserStartOrStopTyping(Long chatId, Long userId, String userName,
        Boolean userIsTexting) {
        UserIsTypingDTOResponse response = new UserIsTypingDTOResponse(chatId, userId, userName, userIsTexting);
        log.info("onUserStartOrStopTyping response: {}", response);
        String destination = String.format("/countries/%s/texting-users", chatId);

        messagingTemplate.convertAndSend(destination, response);
    }

    @Override
    public User save(User user) throws IOException {
        String password = user.getPassword();
        String encodePassword = passwordEncoder.encode(password);
        user.setPassword(encodePassword);
        return repository.save(user);
    }

    @Override
    public User update(User user) {
        var existingUser = findUserById(user.getId());
        processEmailChange(user,existingUser);
        updateSecurityInfo(user, existingUser);
        return repository.save(user);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return repository.findByUserEmail(email);
    }

    @Override
    public User findById(Long userId) {
        return repository.findById(userId).orElseThrow(
                () -> new NoSuchElementException("Can not find user by ID: " + userId)
        );
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.findByUserEmail(email).isPresent();
    }

    private User findUserById(Long userId) {
        return repository.findById(userId)
                .orElseThrow(
                        () -> new NoSuchElementException("Can not find user by ID: " + userId)
                );
    }

    private void updateSecurityInfo(User user, User existingUser) {
        user.setPassword(existingUser.getPassword());
        user.setRole(existingUser.getRole());
    }

    private void processEmailChange(User user,User existingUser) {
        if (isEmailChanged(existingUser.getUserEmail(), user.getUserEmail())) {
            validateNewEmail(user.getUserEmail());
        }
    }

    private void checkDuplicateEmail(String email) {
        var userByEmail = findUserByEmail(email);
        if (userByEmail.isPresent()) {
            throw new AuthenticationException("A user with this email already exists");
        }
    }

    private void validateNewEmail(String newEmail) {
        checkDuplicateEmail(newEmail);
        validateEmailFormat(newEmail);
    }

    private void validateEmailFormat(String email) {
        if (!emailValidator.isValid(email)) {
            throw new RegistrationException("Invalid email address");
        }
    }

    public boolean isEmailChanged(String oldEmail, String newEmail) {
        return newEmail != null && !oldEmail.equals(newEmail);
    }
}
