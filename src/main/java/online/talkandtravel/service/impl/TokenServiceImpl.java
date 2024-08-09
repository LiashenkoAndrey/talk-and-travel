package online.talkandtravel.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import online.talkandtravel.model.entity.Token;
import online.talkandtravel.repository.TokenRepository;
import online.talkandtravel.service.TokenService;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link TokenService} for managing authentication and authorization tokens.
 *
 * <p>This service provides methods for CRUD operations related to tokens, including saving,
 * retrieving, and deleting tokens. It ensures token validity and handles batch operations on
 * tokens.
 *
 * <p>The service includes the following functionalities:
 *
 * <ul>
 *   <li>{@link #save(Token)} - Saves a single token to the repository.
 *   <li>{@link #findAllValidTokensByUserId(Long)} - Retrieves a list of valid tokens associated
 *       with a specific user.
 *   <li>{@link #findByToken(String)} - Finds a token by its value, returning an {@code Optional} to
 *       handle the case where the token might not exist.
 *   <li>{@link #saveAll(List)} - Saves a batch of tokens to the repository.
 *   <li>{@link #deleteInvalidTokensByUserId(Long)} - Deletes all invalid tokens associated with a
 *       specific user.
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
  private final TokenRepository repository;

  @Override
  public Token save(Token token) {
    return repository.save(token);
  }

  @Override
  public List<Token> findAllValidTokensByUserId(Long userId) {
    return repository.findAllValidTokensByUserId(userId);
  }

  @Override
  public Optional<Token> findByToken(String token) {
    return repository.findByToken(token);
  }

  @Override
  public List<Token> saveAll(List<Token> tokens) {
    return repository.saveAll(tokens);
  }

  @Override
  public void deleteInvalidTokensByUserId(Long userId) {
    repository.deleteInvalidTokensByUserId(userId);
  }
}
