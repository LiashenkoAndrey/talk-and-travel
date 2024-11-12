package online.talkandtravel.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import online.talkandtravel.model.entity.Token;
import online.talkandtravel.model.entity.User;

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
 *   <li>{@link #findByToken(String)} - Finds a token by its string value. This operation is useful
 *       for validating tokens or performing operations based on a specific token.
 *   <li>{@link #saveAll(List)} - Saves a list of tokens to the repository. This operation can be
 *       used for bulk token insertion or updating.
 * </ul>
 */
public interface TokenService {

  void deleteUserToken(User user);

  Token save(Token token);

  Optional<Token> findByToken(String token);

  List<Token> saveAll(List<Token> tokens);

  Long validateTokenAndGetUserId(String token);

  Long extractId(String token);

  String generateToken(Long userId);

  String generateToken(Map<String, Object> extraClaims, Long userId);

  Token generatePasswordRecoveryToken(User user);

  void checkTokenIsExpired(Token token);

  void deleteToken(Token token);

  Token getToken(String token);

  Token saveNewToken(String jwtToken, User user);
}
