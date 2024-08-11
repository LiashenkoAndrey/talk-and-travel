package online.talkandtravel.controller.http;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.dto.chat.ChatInfoDto;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.service.ChatService;
import online.talkandtravel.util.constants.ApiPathConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling HTTP requests related to chat functionalities.
 *
 * <ul>
 *   <li>{@code findAllChats} - Retrieves a paginated list of all available chats.
 *   <li>{@code findMainChat} - Finds and returns the main chat for a specific country.
 *   <li>{@code findUserCount} - Returns the number of users in a specific chat, identified by its
 *       ID.
 *   <li>{@code findUserChats} - Retrieves a list of chats associated with a specific user,
 *       identified by their user ID.
 *   <li>{@code findUsersByChatId} - Provides a list of users participating in a specific chat,
 *       identified by its ID.
 *   <li>{@code getChatMessagesOrderedByDate} - Retrieves paginated messages from a specific chat,
 *       ordered by date.
 * </ul>
 */
@RestController
@RequestMapping(ApiPathConstants.API_BASE_PATH + "/chats")
@RequiredArgsConstructor
@Log4j2
public class ChatController {

  private final ChatService chatService;

  @GetMapping
  public Page<ChatInfoDto> findAllChats(@PageableDefault Pageable pageable) {
    return chatService.findAllChats(pageable);
  }

  @GetMapping("/{country}/main")
  public ChatDto findMainChat(@PathVariable("country") String country) {
    return chatService.findMainChat(country);
  }

  @GetMapping("/{chatId}/user-count")
  public Long findUserCount(@PathVariable("chatId") Long chatId) {
    return chatService.countUsersInChat(chatId);
  }

  @GetMapping("/user/{userId}")
  public List<ChatInfoDto> findUserChats(@PathVariable("userId") Long userId) {
    return chatService.findUserChats(userId);
  }

  @GetMapping("/{chatId}/users")
  public List<UserDtoBasic> findUsersByChatId(@PathVariable("chatId") Long chatId) {
    return chatService.findAllUsersByChatId(chatId);
  }

  @GetMapping("/{chatId}/messages")
  public Page<MessageDtoBasic> getChatMessagesOrderedByDate(
      @PathVariable("chatId") Long chatId, @PageableDefault Pageable pageable) {
    return chatService.findAllMessagesInChatOrdered(chatId, pageable);
  }
}
