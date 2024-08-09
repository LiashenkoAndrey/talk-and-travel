package online.talkandtravel.repository;

import online.talkandtravel.model.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Repository interface for managing {@link Chat} entities.
 *
 * <p>This interface provides methods to perform CRUD operations on {@code Chat} entities,
 * including:
 *
 * <ul>
 *   <li>Standard CRUD operations inherited from {@link JpaRepository}.
 *   <li>{@code countChats()} - Returns the total count of {@code Chat} entities.
 * </ul>
 */
public interface ChatRepository extends JpaRepository<Chat, Long> {

  @Query("SELECT COUNT(c) FROM Chat c")
  long countChats();
}
