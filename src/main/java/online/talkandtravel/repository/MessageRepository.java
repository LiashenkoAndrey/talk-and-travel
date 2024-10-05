package online.talkandtravel.repository;

import java.time.LocalDateTime;
import java.util.Optional;
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

  Page<Message> findAllByChatIdAndCreationDateLessThanEqual(Long chatId, LocalDateTime creationDate, Pageable pageable);

  Optional<Message> findFirstByChatIdOrderByCreationDateDesc(Long chatId);

  Page<Message> findAllByChatIdAndCreationDateAfter(Long chatId, LocalDateTime creationDate, Pageable pageable);

  Page<Message> findAllByChatId(Long chatId, Pageable pageable);

  long countAllByChatIdAndIdGreaterThan(Long chatId, Long lastReadMessageId);

  long countAllByChatIdAndCreationDateAfter(Long chatId, LocalDateTime creationDate);

}
