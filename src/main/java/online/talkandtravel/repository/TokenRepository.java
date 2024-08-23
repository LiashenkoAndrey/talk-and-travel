package online.talkandtravel.repository;

import java.util.Optional;
import online.talkandtravel.model.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Token} entities.
 *
 * <p>This interface provides methods to perform CRUD operations on {@code Token} entities,
 * including:
 *
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

  Optional<Token> findByToken(String token);

  Optional<Token> findByUserUserEmail(String user_userEmail);

  void deleteAllByUserId(Long userId);
}
