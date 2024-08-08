package online.talkandtravel.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import online.talkandtravel.exception.chat.ChatNotFoundException;
import online.talkandtravel.exception.user.UserAlreadyJoinTheChatException;
import online.talkandtravel.exception.user.UserNotFoundException;
import online.talkandtravel.model.dto.event.EventDtoBasic;
import online.talkandtravel.model.dto.event.JoinChatRequest;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.Event;
import online.talkandtravel.model.entity.EventType;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserChat;
import online.talkandtravel.model.entity.UserCountry;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.EventRepository;
import online.talkandtravel.repository.UserChatRepository;
import online.talkandtravel.repository.UserCountryRepository;
import online.talkandtravel.repository.UserRepository;
import online.talkandtravel.service.EventService;
import online.talkandtravel.util.mapper.EventMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
  private final ChatRepository chatRepository;
  private final EventRepository eventRepository;
  private final UserRepository userRepository;
  private final EventMapper eventMapper;
  private final UserChatRepository userChatRepository;
  private final UserCountryRepository userCountryRepository;

  @Transactional
  @Override
  public EventDtoBasic joinChat(JoinChatRequest request) {
    Chat chat = getChat(request);
    User author = getUser(request);
    checkUserAlreadyJoinedChat(request);

    saveConnections(chat, author);

    Event event = Event.builder().chat(chat).user(author).eventType(EventType.JOIN).build();
    event = eventRepository.save(event);
    return eventMapper.toEventDtoBasic(event);
  }

  private void saveConnections(Chat chat, User author) {
    Optional<UserCountry> userCountryOptional =
        userCountryRepository.findByCountryNameAndUserId(
            chat.getCountry().getName(), author.getId());

    UserCountry userCountry =
        userCountryOptional.orElseGet(
            () -> UserCountry.builder()
                .country(chat.getCountry())
                .user(author)
                .build());

    UserChat userChat = UserChat.builder()
        .chat(chat)
        .user(author)
        .userCountry(userCountry)
        .build();

    userCountry.getChats().add(userChat);

    // save connection user with chat
    userCountryRepository.save(userCountry);
  }

  private void checkUserAlreadyJoinedChat(JoinChatRequest request) {
    List<UserChat> userChats =
        userChatRepository.findAllByChatIdAndUserId(request.chatId(), request.authorId());
    if (!userChats.isEmpty()) {
      throw new UserAlreadyJoinTheChatException(request.authorId(), request.chatId());
    }
  }

  private User getUser(JoinChatRequest request) {
    return userRepository
        .findById(request.authorId())
        .orElseThrow(() -> new UserNotFoundException(request.authorId()));
  }

  private Chat getChat(JoinChatRequest request) {
    return chatRepository
        .findById(request.chatId())
        .orElseThrow(() -> new ChatNotFoundException(request.chatId()));
  }
}
