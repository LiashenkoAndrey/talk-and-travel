package online.talkandtravel.service;

import java.util.List;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatService {

  Page<ChatDto> findAllChats(Pageable pageable);

  void saveChatList(List<Chat> countryChats);

  long countAllChats();
}
