package online.talkandtravel.repository;

import java.util.List;
import java.util.Optional;
import online.talkandtravel.model.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Token} entities.
 *
 * <p>This interface provides methods to perform CRUD operations on {@code Token} entities,
 * including:
 *
 * <ul>
 *   <li>Retrieving all valid tokens for a given user through {@link
 *       #findAllValidTokensByUserId(Long)}.
 *   <li>Finding a token by its value through {@link #findByToken(String)}.
 *   <li>Deleting invalid tokens (expired or revoked) for a user through {@link
 *       #deleteInvalidTokensByUserId(Long)}.
 * </ul>
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
  @Query(
      "SELECT t "
          + "from Token t "
          + "JOIN User u "
          + "ON t.user.id = u.id "
          + "WHERE u.id =:userId "
          + "AND (t.expired = false OR t.revoked = false )")
  List<Token> findAllValidTokensByUserId(Long userId);

  Optional<Token> findByToken(String token);

  Optional<Token> findByUserUserEmail(String user_userEmail);

  @Modifying
  @Query(
      "DELETE "
          + "FROM Token t "
          + "WHERE t.user.id = :userId "
          + "AND (t.expired = true OR t.revoked = true)")
  void deleteInvalidTokensByUserId(Long userId);
}
