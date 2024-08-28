package online.talkandtravel.repository;

import java.util.List;
import java.util.Optional;
import online.talkandtravel.model.entity.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link UserChat} entities.
 *
 * <p>This interface provides methods to perform CRUD operations on {@code UserChat} entities,
 * including:
 * <ul>
 *   <li>Finding all user-chat associations for a given user through {@link #findAllByUserId(Long)}.</li>
 *   <li>Finding a specific user-chat association by chat ID and user ID through {@link #findByChatIdAndUserId(Long, Long)}.</li>
 *   <li>Finding all user-chat associations for a given user and user country ID through {@link #findAllByUserIdAndUserCountryId(Long, Long)}.</li>
 * </ul>
 */

public interface UserChatRepository extends JpaRepository<UserChat, Long> {

  List<UserChat> findAllByUserId(Long userId);

  Optional<UserChat> findByChatIdAndUserId(Long chatId, Long userId);

  List<UserChat> findAllByUserIdAndUserCountryId(Long userId, Long countryId);

  List<UserChat> findAllByChatId(Long chatId);
}
