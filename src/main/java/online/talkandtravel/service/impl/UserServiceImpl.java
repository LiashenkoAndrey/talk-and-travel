package online.talkandtravel.service.impl;

import online.talkandtravel.exception.auth.AuthenticationException;
import online.talkandtravel.exception.auth.RegistrationException;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.validator.UserEmailValidator;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
/**
 * Implementation of the {@link UserService} for managing user-related operations such as
 * saving, updating, and retrieving user information.
 *
 * <p>This service handles user registration, profile updates, and email validation. It ensures
 * that password security is maintained and that email addresses adhere to the required format
 * and uniqueness constraints.
 *
 * <p>The service includes the following functionalities:
 * <ul>
 *   <li>{@link #save(User)} - Encrypts the user's password and saves the user to the repository.</li>
 *   <li>{@link #update(User)} - Updates an existing user's information, including handling email changes
 *       and preserving security information like the password and role.</li>
 *   <li>{@link #findUserByEmail(String)} - Retrieves a user by their email address, returning an
 *       {@code Optional} to handle the case where the user might not exist.</li>
 *   <li>{@link #findById(Long)} - Finds a user by their ID, throwing a {@link NoSuchElementException}
 *       if the user does not exist.</li>
 *   <li>{@link #existsByEmail(String)} - Checks if a user with the specified email address exists.</li>
 * </ul>
 *
 * <p>Private methods include:
 * <ul>
 *   <li>{@link #findUserById(Long)} - Finds a user by ID and throws a {@link NoSuchElementException}
 *       if the user does not exist.</li>
 *   <li>{@link #updateSecurityInfo(User, User)} - Updates the security information of the user, such
 *       as password and role, based on the existing user information.</li>
 *   <li>{@link #processEmailChange(User, User)} - Handles the process of changing a user's email,
 *       including validation and checking for duplicates.</li>
 *   <li>{@link #checkDuplicateEmail(String)} - Checks for duplicate email addresses and throws
 *       {@link AuthenticationException} if an email already exists.</li>
 *   <li>{@link #validateNewEmail(String)} - Validates a new email address by checking for duplicates
 *       and ensuring proper format.</li>
 *   <li>{@link #validateEmailFormat(String)} - Validates the format of an email address using
 *       {@link UserEmailValidator}.</li>
 *   <li>{@link #isEmailChanged(String, String)} - Determines if the user's email has been changed.</li>
 * </ul>
 *
 */

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
