package online.talkandtravel.service;

import java.util.List;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatService {

  Page<ChatDto> findAllChats(Pageable pageable);

  ChatDto findMainChat(String countryName);

  Long countUsersInChat(Long chatId);

  List<ChatDto> findUserChats(Long userId);

  List<UserDtoBasic> findAllUsersByChatId(Long chatId);

  ChatDto findChatById(Long chatId);
}
