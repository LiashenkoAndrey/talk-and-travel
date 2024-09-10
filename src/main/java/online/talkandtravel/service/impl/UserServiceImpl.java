package online.talkandtravel.service.impl;

import java.time.Duration;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.dto.user.UserOnlineStatusDto;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserOnlineStatus;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.security.CustomUserDetails;
import online.talkandtravel.service.AuthenticationService;
import online.talkandtravel.service.UserService;
import online.talkandtravel.util.mapper.UserMapper;
import online.talkandtravel.util.service.EventPublisherUtil;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static online.talkandtravel.util.service.EventDestination.USER_STATUS_KEY;
import static online.talkandtravel.util.service.EventDestination.getUserStatusKey;


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
@Log4j2
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final RedisTemplate<String, String> redisTemplate;
  private final AuthenticationService authenticationService;

  private final EventPublisherUtil publisherUtil;

  public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
      UserMapper userMapper, RedisTemplate<String, String> redisTemplate,
      AuthenticationService authenticationService, @Lazy EventPublisherUtil publisherUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userMapper = userMapper;
    this.redisTemplate = redisTemplate;
    this.authenticationService = authenticationService;
    this.publisherUtil = publisherUtil;
  }

  @Override
  public UserOnlineStatusDto getUserOnlineStatus(Long userId) {
    String isOnline = redisTemplate.opsForValue().get(getUserStatusKey(userId));
    return new UserOnlineStatusDto(userId, Boolean.getBoolean(isOnline));
  }

  @Override
  public void updateUserOnlineStatus(UserOnlineStatus onlineStatus, User existingUser) {
    try {
      log.info("update user online status with id:{}, isOnline:{}",
              existingUser.getId(), onlineStatus.toString());

      updateUserOnlineStatus(onlineStatus.isOnline(), existingUser.getId());
    } catch (Exception e) {
      log.error("updateUserOnlineStatus: {} ", e.getMessage());
    }
  }

  /**
   * Updates the online status of a user by their user ID.
   * This method stores the user's online status in Redis and sets an expiration duration
   * based on the provided status.
   * <ul>
   *    <li>If the user is online (`isOnline` is true), an expiration duration is set for the key.
   *    <li>If the user is offline (`isOnline` is false), the key will be set without an expiration.
   * </ul>
   *
   * @param isOnline The online status of the user (true if online, false if offline).
   * @param userId The unique ID of the user whose status is being updated.
   */
  private void updateUserOnlineStatus(Boolean isOnline, Long userId) {
    String key = String.format(USER_STATUS_KEY, userId);
    if (isOnline) {
      Duration expirationDuration = publisherUtil.getUserOnlineStatusExpirationDuration();
      redisTemplate.opsForValue().set(key, isOnline.toString(), expirationDuration);
    } else {
      redisTemplate.opsForValue().set(key, isOnline.toString());
    }
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

  /**
   * encodes a user password stored in a {@link User} object
   */
  private void encodePassword(User user) {
    String password = user.getPassword();
    String encodePassword = passwordEncoder.encode(password);
    user.setPassword(encodePassword);
  }

}
