package online.talkandtravel.controller.http;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.service.ChatService;
import online.talkandtravel.util.constants.ApiPathConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPathConstants.API_BASE_PATH + "/chats")
@RequiredArgsConstructor
@Log4j2
public class ChatController {

  private final ChatService chatService;

  @GetMapping
  public ResponseEntity<Page<ChatDto>> findAllChats(@PageableDefault Pageable pageable) {
    return ResponseEntity.ok(chatService.findAllChats(pageable));
  }

  @GetMapping("/{country}/main")
  public ResponseEntity<ChatDto> findMainChat(@PathVariable("country") String country) {
    return ResponseEntity.ok(chatService.findMainChat(country));
  }

  @GetMapping("/{chatId}/user-count")
  public ResponseEntity<Long> findUserCount(@PathVariable("chatId") Long chatId) {
    return ResponseEntity.ok(chatService.countUsersInChat(chatId));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<ChatDto>> findUserChats(@PathVariable("userId") Long userId) {
    return ResponseEntity.ok(chatService.findUserChats(userId));
  }

  @GetMapping("/{chatId}/users")
  public ResponseEntity<List<UserDtoBasic>> findUsersByChatId(@PathVariable("chatId") Long chatId) {
    return ResponseEntity.ok(chatService.findAllUsersByChatId(chatId));
  }

  @GetMapping("/{chatId}/messages")
  public ResponseEntity<Page<MessageDtoBasic>> getChatMessagesOrderedByDate(
      @PathVariable("chatId") Long chatId, @PageableDefault Pageable pageable) {
    return ResponseEntity.ok( chatService.findAllMessagesInChatOrdered(chatId, pageable));
  }
}
