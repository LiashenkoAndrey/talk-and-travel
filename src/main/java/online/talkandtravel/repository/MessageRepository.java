package online.talkandtravel.repository;

import online.talkandtravel.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Message} entities.
 *
 * <p>This interface provides methods to perform CRUD operations on {@code Message} entities,
 * including:
 *
 * <ul>
 *   <li>Standard CRUD operations inherited from {@link JpaRepository}.
 *   <li>Pagination support for retrieving messages by chat ID through {@link #findAllByChatId(Long,
 *       Pageable)}.
 * </ul>
 */
public interface MessageRepository extends JpaRepository<Message, Long> {

  Page<Message> findAllByChatId(Long chatId, Pageable pageable);

}
