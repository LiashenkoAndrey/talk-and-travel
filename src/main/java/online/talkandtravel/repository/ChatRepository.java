package online.talkandtravel.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.ChatType;
import online.talkandtravel.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  Page<Chat> findAllByChatType(ChatType chatType, Pageable pageable);

  @Query("SELECT COUNT(*) FROM Message m WHERE m.id > :lastReadMessageId AND m.chat.id = :chatId")
  long countUnreadMessages(@Param("lastReadMessageId") Long lastReadMessageId, @Param("chatId") Long chatId);

  @Query("SELECT COUNT(c) FROM Chat c")
  long countChats();

  @Query(
      "SELECT c FROM Chat c JOIN c.users u WHERE c.chatType = :chatType AND SIZE(c.users) = 2 AND u.id IN :userIds GROUP BY c.id HAVING COUNT(u.id) = 2")
  Optional<Chat> findChatByUsersAndChatType(List<Long> userIds, ChatType chatType);
}
