package online.talkandtravel.controller.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.service.ChatService;
import online.talkandtravel.util.constants.ApiPathConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPathConstants.API_BASE_PATH + "/chats")
@RequiredArgsConstructor
@Log4j2
public class ChatController {

  private final ChatService chatService;

  @GetMapping
  public ResponseEntity<Page<ChatDto>> findAllChats(@PageableDefault Pageable pageable){
    return ResponseEntity.ok(chatService.findAllChats(pageable));
  }
}
