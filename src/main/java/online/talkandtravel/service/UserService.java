package online.talkandtravel.service;

import java.io.IOException;
import java.util.Optional;
import online.talkandtravel.model.entity.User;

/**
 * Service interface for managing user entities within the application.
 *
 * <p>This service provides methods for performing various user-related operations, including saving
 * and updating user details, finding users by email or ID, and checking for the existence of users
 * based on their email address.
 *
 * <p>Methods:
 *
 * <ul>
 *   <li>{@link #save(User)} - Saves a new user or updates an existing user's information. The
 *       password of the user is encoded before being saved. Throws {@link IOException} if there is
 *       an error during the saving process.
 *   <li>{@link #update(User)} - Updates the details of an existing user. The method performs checks
 *       and updates user information while preserving the existing password and role. Returns the
 *       updated user entity.
 *   <li>{@link #findUserByEmail(String)} - Retrieves a user by their email address. Returns an
 *       {@link Optional} that contains the user if found, or empty if no user with the specified
 *       email exists.
 *   <li>{@link #findById(Long)} - Finds a user by their unique ID.
 *   <li>{@link #existsByEmail(String)} - Checks if a user with the specified email address already
 *       exists in the system. Returns true if such a user exists, false otherwise.
 * </ul>
 */
public interface UserService {

  User save(User user);

  User update(User user);

  Optional<User> findUserByEmail(String email);

  User findById(Long userId);

  boolean existsByEmail(String email);
}
