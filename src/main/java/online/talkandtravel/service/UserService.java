package online.talkandtravel.service;

import java.util.List;
import java.util.Optional;
import online.talkandtravel.model.dto.auth.RegisterRequest;
import online.talkandtravel.model.dto.auth.SocialRegisterRequest;
import online.talkandtravel.model.dto.user.UpdateUserRequest;
import online.talkandtravel.model.dto.user.UpdateUserResponse;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoShort;
import online.talkandtravel.model.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for managing user entities within the application.
 *
 * <p>This service provides methods for performing various user-related operations, including saving
 * and updating user details, finding users by email or ID, and checking for the existence of users
 * based on their email address.
 */
public interface UserService {

  void checkUserExistByEmail(String email);

  List<UserDtoShort> getAllUsers();

  void updateLastLoggedOnToNow(User user);

  UserDtoBasic saveNewUser(RegisterRequest request);

  UserDtoBasic saveNewUser(SocialRegisterRequest request);

  UserDtoBasic mapToUserDtoBasic(User user);

  UserDtoBasic save(User user);

  User getUserById(Long userId);

  UserDetails getUserDetails(Long userId);

  UpdateUserResponse update(UpdateUserRequest request);

  Optional<User> findUserByEmail(String email);

  UserDtoBasic findById(Long userId);

  boolean existsByEmail(String email);

  User getUser(String email);

  void updateUserPassword(User user, String rawPassword);

  void saveUserRegisterDataToTempStorage(String token, RegisterRequest request);

  RegisterRequest getUserRegisterDataFromTempStorage(String token);

  void checkForDuplicateEmail(String userEmail);

  void deleteUser(User user);
}
