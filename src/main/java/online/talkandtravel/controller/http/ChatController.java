package online.talkandtravel.controller.http;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.dto.chat.ChatInfoDto;
import online.talkandtravel.model.dto.chat.NewPrivateChatDto;
import online.talkandtravel.model.dto.chat.NewChatDto;
import online.talkandtravel.model.dto.chat.PrivateChatDto;
import online.talkandtravel.model.dto.chat.PrivateChatInfoDto;
import online.talkandtravel.model.dto.chat.SetLastReadMessageRequest;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.service.ChatService;
import online.talkandtravel.util.constants.ApiPathConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling HTTP requests related to chat functionalities.
 *
 * <ul>
 *   <li>{@link #createPrivateChat} - creates private chat between two users
 *   <li>{@link #findAllChats} - Retrieves a paginated list of all available chats.
 *   <li>{@link #findMainChat} - Finds and returns the main chat for a specific country.
 *   <li>{@link #findUserCount} - Returns the number of users in a specific chat,
 *   identified by its ID.
 *   <li>{@link #getChatMessagesOrderedByDate}</li> - gets chat messages ordered by date
 *   <li>{@link #updateLastReadMessage}</li> - updates lastReadMessage of  field that represents
 *   last read message of chat by user
 *   <li>{@link #getReadMessages}</li> - finds messages that the user has already read
 *   <li>{@link #getUnreadMessages}</li> - finds messages that the user has not yet read
 *   <li>{@link #findUserChats} - Retrieves a list of chats associated with a specific user,
 *       identified by their user ID.
 *   <li>{@link #findUsersByChatId} - Provides a list of users participating in a specific chat,
 *       identified by its ID.
 *   <li>{@link #getChatMessagesOrderedByDate} - Retrieves paginated messages from a specific chat,
 *       ordered by date.
 * </ul>
 */
@RestController
@RequestMapping(ApiPathConstants.API_BASE_PATH + "/chats")
@RequiredArgsConstructor
@Log4j2
public class ChatController {

  private final ChatService chatService;

  @PostMapping
  public ChatDto createCountryChat(@RequestBody @Valid NewChatDto dto) {
    return chatService.createCountryChat(dto);
  }

  /**
   * creates a private chat between two users
   * @return chat id
   */
  @PostMapping("/private")
  public Long createPrivateChat(@Valid @RequestBody NewPrivateChatDto dto) {
    return chatService.createPrivateChat(dto);
  }

  @GetMapping
  public Page<ChatInfoDto> findAllChats(@PageableDefault Pageable pageable) {
    return chatService.findAllGroupChats(pageable);
  }

  @GetMapping("/{chatId}/users")
  public List<UserDtoBasic> findUsersByChatId(@PathVariable Long chatId) {
    return chatService.findAllUsersByChatId(chatId);
  }

  @GetMapping("/{chatId}/user-count")
  public Long findUserCount(@PathVariable Long chatId) {
    return chatService.countUsersInChat(chatId);
  }

  @GetMapping("/{chatId}/messages")
  public Page<MessageDtoBasic> getChatMessagesOrderedByDate(
      @PathVariable Long chatId, @PageableDefault Pageable pageable) {
    return chatService.findAllMessagesInChatOrdered(chatId, pageable);
  }

  /**
   * updates id of last read message of chat by user
   * @param dtoRequest userId and lastReadMessageId
   */
  @PutMapping("/{chatId}/messages/last-read")
  public void updateLastReadMessage(
      @PathVariable @Positive @NotNull Long chatId,
      @RequestBody @Valid SetLastReadMessageRequest dtoRequest) {
    chatService.setLastReadMessage(chatId, dtoRequest);
  }

  /** finds messages that was before specified last read message (including last read message) */
  @GetMapping("/{chatId}/messages/read")
  public Page<MessageDtoBasic> getReadMessages(
      @PathVariable Long chatId,
      @RequestParam Long lastReadMessageId,
      @PageableDefault(sort = "creationDate") Pageable pageable) {
    return chatService.findReadMessages(chatId, lastReadMessageId, pageable);
  }

  /** finds messages that was sent after specified last read message */
  @GetMapping("/{chatId}/messages/unread")
  public Page<MessageDtoBasic> getUnreadMessages(
      @PathVariable Long chatId,
      @RequestParam @Positive Long lastReadMessageId,
      @PageableDefault(sort = "creationDate") Pageable pageable) {
    return chatService.findUnreadMessages(chatId, lastReadMessageId, pageable);
  }

  @GetMapping("/user/{userId}")
  public List<PrivateChatInfoDto> findUserChats(@PathVariable Long userId) {
    return chatService.findUserChats(userId);
  }

  @GetMapping("/user/{userId}/private")
  public List<PrivateChatDto> getPrivateChats(@PathVariable Long userId) {
    return chatService.findAllUsersPrivateChats(userId);
  }

  @GetMapping("/{country}/main")
  public ChatDto findMainChat(@PathVariable String country) {
    return chatService.findMainChat(country);
  }
}
