package online.talkandtravel.controller.http;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.dto.chat.ChatInfoDto;
import online.talkandtravel.model.dto.chat.NewChatDto;
import online.talkandtravel.model.dto.chat.NewPrivateChatDto;
import online.talkandtravel.model.dto.chat.PrivateChatDto;
import online.talkandtravel.model.dto.chat.SetLastReadMessageRequest;
import online.talkandtravel.model.dto.message.MessageDto;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.service.ChatService;
import online.talkandtravel.util.constants.ApiPathConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class responsible for handling HTTP requests related to chat functionalities.
 *
 * <ul>
 *   <li>{@link #createPrivateChat} - creates private chat between two users
 *   <li>{@link #findAllChats} - Retrieves a paginated list of all available chats.
 *   <li>{@link #findMainChat} - Finds and returns the main chat for a specific country.
 *   <li>{@link #findUserCount} - Returns the number of users in a specific chat, identified by its
 *       ID.
 *   <li>{@link #getChatMessagesOrderedByDate} - gets chat messages ordered by date
 *   <li>{@link #updateLastReadMessage} - updates lastReadMessage of field that represents last read
 *       message of chat by user
 *   <li>{@link #getReadMessages} - finds messages that the user has already read
 *   <li>{@link #getUnreadMessages} - finds messages that the user has not yet read
 *   <li>{@link #findUserChats} - Retrieves a list of chats associated with a specific user,
 *       identified by their user ID.
 *   <li>{@link #findUsersByChatId} - Provides a list of users participating in a specific chat,
 *       identified by its ID.
 *   <li>{@link #getChatMessagesOrderedByDate} - Retrieves paginated messages from a specific chat,
 *       ordered by date.
 * </ul>
 */
@RestController
@RequestMapping(ApiPathConstants.API_BASE_PATH )
@RequiredArgsConstructor
@Log4j2
public class ChatController {

  private final ChatService chatService;

  @PostMapping("/chats")
  public ChatDto createCountryChat(@RequestBody @Valid NewChatDto dto) {
    return chatService.createCountryChat(dto);
  }

  /**
   * creates a private chat between two users
   *
   * @return chat id
   */
  @PostMapping("/chats/private")
  public Long createPrivateChat(@Valid @RequestBody NewPrivateChatDto dto) {
    return chatService.createPrivateChat(dto);
  }

  @GetMapping("/chats")
  public Page<ChatInfoDto> findAllChats(@PageableDefault Pageable pageable) {
    return chatService.findAllGroupChats(pageable);
  }

  @GetMapping("/chats/{chatId}/users")
  public List<UserDtoBasic> findUsersByChatId(@PathVariable Long chatId) {
    return chatService.findAllUsersByChatId(chatId);
  }

  @GetMapping("/chats/{chatId}/user-count")
  public Long findUserCount(@PathVariable Long chatId) {
    return chatService.countUsersInChat(chatId);
  }

  @GetMapping("/chats/{chatId}/messages")
  public Page<MessageDto> getChatMessagesOrderedByDate(
      @PathVariable Long chatId,
      @PageableDefault Pageable pageable) {
    return chatService.findAllMessagesInChatOrdered(chatId, pageable);
  }

  /**
   * updates id of last read message of chat by user
   *
   * @param dtoRequest userId and lastReadMessageId
   */
  @PatchMapping("/chats/{chatId}/messages/last-read")
  public ResponseEntity<Void> updateLastReadMessage(
      @PathVariable @Positive @NotNull Long chatId,
      @RequestBody @Valid SetLastReadMessageRequest dtoRequest) {
    chatService.setLastReadMessage(chatId, dtoRequest);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  /** finds messages that was before specified last read message (including last read message) */
  @GetMapping("/chats/{chatId}/messages/read")
  public Page<MessageDto> getReadMessages(
      @PathVariable Long chatId,
      @PageableDefault(sort = "creationDate") Pageable pageable) {
    return chatService.findReadMessages(chatId, pageable);
  }

  /** finds messages that was sent after specified last read message */
  @GetMapping("/chats/{chatId}/messages/unread")
  public Page<MessageDto> getUnreadMessages(
      @Positive @PathVariable Long chatId,
      @PageableDefault(sort = "creationDate") Pageable pageable) {
    return chatService.findUnreadMessages(chatId, pageable);
  }

  @GetMapping({"/chats/user/{userId}", "/v2/user/chats"})
  public List<ChatInfoDto> findUserChats() {
    return chatService.findUserChats();
  }

  @GetMapping({"/chats/user/{userId}/private", "/v2/user/private-chats"})
  public List<PrivateChatDto> getUserPrivateChats() {
    return chatService.findAllUsersPrivateChats();
  }

  @GetMapping("/chats/{chatId}")
  public ChatDto findChatById(@PathVariable Long chatId){
    return chatService.findChatById(chatId);
  }

  @GetMapping({"/chats/{countryName}/main", "/v2/country/{countryName}/main-chat"})
  public ChatDto findMainChat(@PathVariable String countryName) {
    return chatService.findMainChat(countryName);
  }
}
