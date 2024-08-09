package online.talkandtravel.service;

import java.io.IOException;
import online.talkandtravel.model.dto.AuthResponse;
import online.talkandtravel.model.entity.User;

/**
 * Service interface for handling user authentication and registration operations.
 *
 * <p>This service is responsible for user registration and login processes, providing mechanisms to
 * create new user accounts and authenticate existing users.
 *
 * <p>Methods:
 *
 * <ul>
 *   <li>{@link #register(User)} - Registers a new user with the provided {@link User} details. This
 *       method handles user creation, including password encryption and any necessary setup. It
 *       returns an {@link AuthResponse} containing information about the authentication result.
 *   <li>{@link #login(String, String)} - Authenticates a user using their email and password. If
 *       the credentials are valid, it returns an {@link AuthResponse} containing authentication
 *       details.
 * </ul>
 *
 */
public interface AuthenticationService {

  AuthResponse register(User user) throws IOException;

  AuthResponse login(String email, String password);
}
