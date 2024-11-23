package online.talkandtravel.repository;

import java.util.Optional;
import java.util.UUID;
import online.talkandtravel.model.entity.Avatar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Avatar} entities.
 *
 * <p>This interface provides methods to perform CRUD operations on {@code Avatar} entities,
 * including:
 *
 * <ul>
 *   <li>Standard CRUD operations inherited from {@link JpaRepository}.
 *   <li>{@code findByUserId(Long userId)} - Finds an avatar by the associated user ID.
 * </ul>
 */
@Repository
public interface AvatarRepository extends JpaRepository<Avatar, Long> {
  Optional<Avatar> findByUserId(Long userId);

  @Modifying
  void deleteByKey(UUID key);
}
