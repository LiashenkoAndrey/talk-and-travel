package online.talkandtravel.repository;

import online.talkandtravel.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

  Page<Message> findAllByChatId(Long chatId, Pageable pageable);
}
