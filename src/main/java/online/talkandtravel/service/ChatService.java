package online.talkandtravel.service;

import online.talkandtravel.model.dto.chat.ChatDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatService {

  Page<ChatDto> findAllChats(Pageable pageable);
}
