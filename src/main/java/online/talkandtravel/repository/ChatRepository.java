package online.talkandtravel.repository;

import online.talkandtravel.model.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {}
