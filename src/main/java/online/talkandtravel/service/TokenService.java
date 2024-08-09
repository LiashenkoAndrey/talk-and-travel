package online.talkandtravel.service;

import java.util.List;
import java.util.Optional;
import online.talkandtravel.model.entity.Token;

/**
 * Service interface for managing authentication tokens within the application.
 *
 * <p>This service handles operations related to storing, retrieving, and deleting tokens. It
 * supports operations for saving tokens, finding valid tokens by user ID, retrieving tokens by
 * their value, saving multiple tokens, and deleting invalid tokens associated with a user.
 *
 * <p>Methods:
 *
 * <ul>
 *   <li>{@link #save(Token)} - Saves a single token to the repository. This operation will persist
 *       a new token or update an existing one.
 *   <li>{@link #findAllValidTokensByUserId(Long)} - Retrieves a list of all valid tokens associated
 *       with a specific user identified by their ID. This is used to manage user sessions and token
 *       validity.
 *   <li>{@link #findByToken(String)} - Finds a token by its string value. This operation is useful
 *       for validating tokens or performing operations based on a specific token.
 *   <li>{@link #saveAll(List)} - Saves a list of tokens to the repository. This operation can be
 *       used for bulk token insertion or updating.
 *   <li>{@link #deleteInvalidTokensByUserId(Long)} - Deletes all tokens that are considered invalid
 *       for a specific user, identified by their ID. This helps in cleaning up old or expired
 *       tokens to maintain security and integrity.
 * </ul>
 */
public interface TokenService {

  Token save(Token token);

  List<Token> findAllValidTokensByUserId(Long userId);

  Optional<Token> findByToken(String token);

  List<Token> saveAll(List<Token> tokens);

  void deleteInvalidTokensByUserId(Long userId);
}
