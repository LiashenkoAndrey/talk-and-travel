package online.talkandtravel.service.impl;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.auth.SocialRegisterRequest;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoShort;
import online.talkandtravel.model.entity.Role;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link UserService} for managing user-related operations such as saving,
 * updating, and retrieving user information.
 *
 * <p>This service handles user registration, profile updates, and email validation. It ensures
 * that password security is maintained and that email addresses adhere to the required format and
 * uniqueness constraints.
 *
 * <p>The service includes the following functionalities:
 * <ul>
 *   <li>{@link UserService#save(User)} - Encrypts the user's password and saves the user to the repository.</li>
 *   <li>{@link #findUserByEmail(String)} - Retrieves a user by their email address, returning an
 *       {@code Optional} to handle the case where the user might not exist.</li>
 *   <li>{@link #findById(Long)} - Finds a user by their ID, throwing a {@link UserNotFoundException}
 *       if the user does not exist.</li>
 *   <li>{@link #existsByEmail(String)} - Checks if a user with the specified email address exists.</li>
 * </ul>
 */

@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final AuthenticationService authenticationService;

  @Override
  public User getUser(String email) {
    return userRepository.findByUserEmail(email)
        .orElseThrow(() -> new UserNotFoundException(email));
  }

  @Override
  public void updateUserPassword(User user, String rawPassword) {
    log.info("Update password of user with id: {}", user.getId());
    String encodedPassword = passwordEncoder.encode(rawPassword);
    user.setPassword(encodedPassword);
    userRepository.save(user);
  }

  @Override
  public void checkUserExistByEmail(String email) {
    getUser(email);
  }

  @Override
  public List<UserDtoShort> getAllUsers() {
    return userRepository.findAll().stream()
        .map(userMapper::toUserDtoShort)
        .toList();
  }

  @Override
  public void updateLastLoggedOnToNow(User user) {
    user.setLastLoggedOn(ZonedDateTime.now(ZoneOffset.UTC));
    userRepository.save(user);
  }

  @Override
  public UserDtoBasic createAndSaveNewUser(RegisterRequest request) {
    User user = userMapper.registerRequestToUser(request);
    user.setRole(Role.USER);
    return save(user);
  }

  @Override
  public UserDtoBasic createAndSaveNewUser(SocialRegisterRequest request) {
    User user = userMapper.registerRequestToUser(request);
    user.setRole(Role.USER);
    return saveFromSocial(user);
  }

  @Override
  public UserDtoBasic mapToUserDtoBasic(User user) {
    return userMapper.toUserDtoBasic(user);
  }

  @Override
  public UserDtoBasic save(User user) {
    encodePassword(user);
    User saved = userRepository.save(user);
    return userMapper.toUserDtoBasic(saved);
  }

  @Override
  public User getReferenceById(Long userId) {
    return userRepository.getReferenceById(userId);
  }

  @Override
  public UserDetails getUserDetails(Long userId) {
    return userRepository.findById(userId)
        .map(CustomUserDetails::new)
        .orElseThrow(() -> new UserNotFoundException(userId));
  }

  /**
   * Updates the details of the given user.
   *
   * @return The updated {@link User} object after being saved to the repository. update another
   * user's data without proper permissions.
   */
  @Override
  @Transactional
  public UpdateUserResponse update(UpdateUserRequest request) {
    User existingUser = authenticationService.getAuthenticatedUser();
    log.info("update user with id:{}, dto:{}", existingUser.getId(), request);
    userMapper.updateUser(request, existingUser);
    User updated = userRepository.save(existingUser);
    return userMapper.toUpdateUserResponse(updated);
  }

  @Override
  public Optional<User> findUserByEmail(String email) {
    return userRepository.findByUserEmail(email);
  }

  @Override
  public UserDtoBasic findById(Long userId) {
    User user = userRepository.findById(userId).orElseThrow(
        () -> new UserNotFoundException(userId)
    );
    return userMapper.toUserDtoBasic(user);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userRepository.findByUserEmail(email).isPresent();
  }

  private UserDtoBasic saveFromSocial(User user) {
    User saved = userRepository.save(user);
    return userMapper.toUserDtoBasic(saved);
  }

  /**
   * encodes a user password stored in a {@link User} object
   */
  private void encodePassword(User user) {
    String password = user.getPassword();
    String encodePassword = passwordEncoder.encode(password);
    user.setPassword(encodePassword);
  }
}
