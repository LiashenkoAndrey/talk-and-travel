package online.talkandtravel.service.impl;

import online.talkandtravel.exception.AuthenticationException;
import online.talkandtravel.exception.RegistrationException;
import online.talkandtravel.model.User;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.validator.UserEmailValidator;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UserEmailValidator emailValidator;

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
