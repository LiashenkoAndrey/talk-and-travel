package online.talkandtravel.service;

import java.util.Map;
import online.talkandtravel.model.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Service interface for JSON Web Token (JWT) operations.
 *
 * <p>This service handles JWT-related tasks such as token generation, validation, and extraction of
 * information from tokens.
 *
 * <p>Methods:
 *
 * <ul>
 *   <li>{@link #extractUsername(String)} - Extracts the username (subject) from the given JWT.
 *   <li>{@link #generateToken(User)} - Generates a JWT for the given user with default claims.
 *   <li>{@link #generateToken(Map, User)} - Generates a JWT for the given user with additional
 *       custom claims.
 *   <li>{@link #isTokenValid(String, UserDetails)} - Validates the given JWT by checking its
 *       validity and ensuring that the token corresponds to the provided user details.
 * </ul>
 *
 * @param token The JWT from which information is extracted or validated.
 * @param user The user for whom the token is generated.
 * @param extraClaims Additional claims to be included in the generated token (used in {@link
 *     #generateToken(Map, User)}).
 * @param userDetails The user details used to validate the token.
 * @return The extracted username or generated token, or a boolean indicating whether the token is
 *     valid.
 */
public interface JwtService {

  String extractUsername(String token);

  String generateToken(User user);

  String generateToken(Map<String, Object> extraClaims, User user);

  Boolean isTokenValid(String token, UserDetails userDetails);
}
