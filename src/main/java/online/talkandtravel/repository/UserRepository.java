package online.talkandtravel.repository;

import java.util.Optional;
import online.talkandtravel.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link User} entities.
 *
 * <p>This interface provides methods to perform CRUD operations on {@code User} entities,
 * including:
 *
 * <ul>
 *   <li>Finding a user by their email address through {@link #findByUserEmail(String)}.
 * </ul>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUserEmail(String email);

  boolean existsByUserEmail(String email);
}
