package online.talkandtravel.repository;

import java.util.List;
import online.talkandtravel.model.entity.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserChatRepository extends JpaRepository<UserChat, Long> {

  List<UserChat> findAllByUserId(Long userId);

  List<UserChat> findAllByChatIdAndUserId(Long chatId, Long userId);
}
