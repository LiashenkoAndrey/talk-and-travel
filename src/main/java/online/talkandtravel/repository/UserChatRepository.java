package online.talkandtravel.repository;

import java.util.List;
import java.util.Optional;
import online.talkandtravel.model.entity.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChatRepository extends JpaRepository<UserChat, Long> {

  List<UserChat> findAllByUserId(Long userId);

  Optional<UserChat> findByChatIdAndUserId(Long chatId, Long userId);

  List<UserChat> findAllByUserIdAndUserCountryId(Long userId, Long countryId);
}
