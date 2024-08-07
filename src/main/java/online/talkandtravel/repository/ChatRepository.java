package online.talkandtravel.repository;

import online.talkandtravel.model.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ChatRepository extends JpaRepository<Chat, Long> {

  @Query("SELECT COUNT(c) FROM Chat c")
  long countChats();
}
