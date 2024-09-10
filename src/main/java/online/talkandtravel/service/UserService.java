package online.talkandtravel.service;

import java.io.IOException;
import java.util.Optional;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.dto.user.UserOnlineStatusDto;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserOnlineStatus;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for managing user entities within the application.
 *
 * <p>This service provides methods for performing various user-related operations, including:
 * saving and updating user details, retrieving user information by email or ID, and checking for
 * the existence of users based on their email address.
 *
 * <p>Methods:
 * <ul>
 *   <li>{@link #getUserOnlineStatus(Long)} - Retrieves the online status of a user by their ID.
 *   <li>{@link #updateUserOnlineStatus(UserOnlineStatus)} - Updates the online status of a user.
 *   <li>{@link #save(User)} - Saves a new user or updates an existing user's information.
 *       The password is encoded before saving. Throws {@link IOException} if there is an error
 *       during the saving process.
 *   <li>{@link #getReferenceById(Long)} - Retrieves a reference to a user by their unique ID.
 *   <li>{@link #getUserDetails(Long)} - Retrieves detailed user information, including security details.
 *   <li>{@link #update(UpdateUserRequest)} - Updates user information based on the provided request.
 *   <li>{@link #findUserByEmail(String)} - Retrieves a user by their email address. Returns an
 *       {@link Optional} containing the user if found, or empty if no user with the specified email exists.
 *   <li>{@link #findById(Long)} - Finds a user by their unique ID.
 *   <li>{@link #existsByEmail(String)} - Checks if a user with the specified email address already
 *       exists in the system. Returns true if such a user exists, false otherwise.
 * </ul>
 */
public interface UserService {

  UserOnlineStatusDto getUserOnlineStatus(Long userId);

  void updateUserOnlineStatus(UserOnlineStatus isOnline, Long userId);

  UserDtoBasic save(User user);

  User getReferenceById(Long userId);

  UserDetails getUserDetails(Long userId);

  UpdateUserResponse update(UpdateUserRequest request);

  Optional<User> findUserByEmail(String email);

  UserDtoBasic findById(Long userId);

  boolean existsByEmail(String email);
}
