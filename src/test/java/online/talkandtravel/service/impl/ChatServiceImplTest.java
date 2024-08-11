package online.talkandtravel.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import online.talkandtravel.exception.chat.ChatNotFoundException;
import online.talkandtravel.exception.chat.MainCountryChatNotFoundException;
import online.talkandtravel.exception.country.CountryNotFoundException;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.dto.chat.ChatInfoDto;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import online.talkandtravel.model.entity.Chat;
import online.talkandtravel.model.entity.ChatType;
import online.talkandtravel.model.entity.Country;
import online.talkandtravel.model.entity.Message;
import online.talkandtravel.model.entity.User;
import online.talkandtravel.model.entity.UserChat;
import online.talkandtravel.repository.ChatRepository;
import online.talkandtravel.repository.CountryRepository;
import online.talkandtravel.repository.MessageRepository;
import online.talkandtravel.repository.UserChatRepository;
import online.talkandtravel.util.mapper.ChatMapper;
import online.talkandtravel.util.mapper.MessageMapper;
import online.talkandtravel.util.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

  @Mock private ChatRepository chatRepository;
  @Mock private UserChatRepository userChatRepository;
  @Mock private CountryRepository countryRepository;
  @Mock private MessageRepository messageRepository;
  @Mock private MessageMapper messageMapper;
  @Mock private ChatMapper chatMapper;
  @Mock private UserMapper userMapper;

  @InjectMocks ChatServiceImpl underTest;

  private Country country;
  private Chat chat;
  private UserChat userChat;
  private User user;
  private ChatInfoDto chatInfoDto;
  private UserDtoBasic userDtoBasic;
  private Message message;
  private MessageDtoBasic messageDtoBasic;
  private Pageable pageable;

  @BeforeEach
  void setUp() {
    chat = new Chat();
    chat.setId(1L);
    chat.setName("TestCountry");

    userChat = new UserChat();
    userChat.setChat(chat);
    user = new User();

    chatInfoDto =
        new ChatInfoDto(
            1L,
            "TestCountry",
            "Test Chat Description",
            ChatType.GROUP,
            LocalDateTime.now(),
            10L,
            0L,
            0L);

    country = new Country();
    country.setName("TestCountry");
    country.setChats(List.of(chat));

    userDtoBasic = new UserDtoBasic(1L, "testUser", "Test User", "test@example.com");

    message = new Message();
    message.setId(1L);
    message.setContent("Test message");

    messageDtoBasic = new MessageDtoBasic(1L, "Test message", LocalDateTime.now(), 1L, 1L, null);

    pageable = PageRequest.of(0, 10);
  }

  @Test
  void findMainChat_shouldThrow_whenCountryNotFound() {
    String countryName = "NonExistentCountry";
    when(countryRepository.findById(countryName)).thenReturn(Optional.empty());

    assertThrows(CountryNotFoundException.class, () -> underTest.findMainChat(countryName));
    verify(countryRepository, times(1)).findById(countryName);
  }

  @Test
  void findMainChat_shouldThrow_whenMainChatNotFound() {
    String countryName = "TestCountry";
    country.setChats(Collections.emptyList());
    when(countryRepository.findById(countryName)).thenReturn(Optional.of(country));

    assertThrows(MainCountryChatNotFoundException.class, () -> underTest.findMainChat(countryName));
    verify(countryRepository, times(1)).findById(countryName);
  }

  @Test
  void findMainChat_shouldFoundMainChat_whenValid() {
    String countryName = "TestCountry";
    ChatDto chatDto =
        new ChatDto(
            1L,
            "TestCountry",
            "Description of TestCountry",
            ChatType.GROUP,
            LocalDateTime.now(),
            100L,
            Collections.emptyList(),
            Collections.emptyList());

    when(countryRepository.findById(countryName)).thenReturn(Optional.of(country));
    when(chatMapper.toDto(chat)).thenReturn(chatDto);

    ChatDto result = underTest.findMainChat(countryName);

    assertEquals(chatDto, result);
    verify(countryRepository, times(1)).findById(countryName);
    verify(chatMapper, times(1)).toDto(chat);
  }

  @Test
  void findUserChats_shouldReturnEmptyList_whenNoChatsFound() {
    Long userId = 1L;
    when(userChatRepository.findAllByUserId(userId)).thenReturn(List.of());

    List<ChatInfoDto> result = underTest.findUserChats(userId);

    assertTrue(result.isEmpty());
    verify(userChatRepository, times(1)).findAllByUserId(userId);
    verifyNoInteractions(chatMapper); // Ensure chatMapper is not called
  }

  @Test
  void findUserChats_shouldReturnChatList_whenChatsFound() {
    Long userId = 1L;

    when(userChatRepository.findAllByUserId(userId)).thenReturn(List.of(userChat));
    when(chatMapper.userChatToInfoDto(userChat)).thenReturn(chatInfoDto);

    List<ChatInfoDto> result = underTest.findUserChats(userId);

    assertEquals(1, result.size());
    assertEquals(chatInfoDto, result.get(0));
    verify(userChatRepository, times(1)).findAllByUserId(userId);
    verify(chatMapper, times(1)).userChatToInfoDto(userChat);
  }

  @Test
  void findAllUsersByChatId_shouldThrow_whenChatNotFound() {
    Long chatId = 1L;
    when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

    assertThrows(ChatNotFoundException.class, () -> underTest.findAllUsersByChatId(chatId));
    verify(chatRepository, times(1)).findById(chatId);
  }

  @Test
  void findAllUsersByChatId_shouldReturnEmptyList_whenNoUsersFound() {
    Long chatId = 1L;
    chat.setUsers(List.of());
    when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));

    List<UserDtoBasic> result = underTest.findAllUsersByChatId(chatId);

    assertTrue(result.isEmpty());
    verify(chatRepository, times(1)).findById(chatId);
  }

  @Test
  void findAllUsersByChatId_shouldReturnUserList_whenUsersFound() {
    Long chatId = 1L;
    chat.setUsers(List.of(user));
    when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
    when(userMapper.toUserDtoBasic(user)).thenReturn(userDtoBasic);

    List<UserDtoBasic> result = underTest.findAllUsersByChatId(chatId);

    assertEquals(1, result.size());
    assertEquals(userDtoBasic, result.get(0));
    verify(chatRepository, times(1)).findById(chatId);
    verify(userMapper, times(1)).toUserDtoBasic(user);
  }

  @Test
  void findAllMessagesInChatOrdered_shouldReturnEmptyPage_whenNoMessagesFound() {
    Long chatId = 1L;
    when(messageRepository.findAllByChatId(chatId, pageable)).thenReturn(Page.empty());

    Page<MessageDtoBasic> result = underTest.findAllMessagesInChatOrdered(chatId, pageable);

    assertTrue(result.isEmpty());
    verify(messageRepository, times(1)).findAllByChatId(chatId, pageable);
    verifyNoInteractions(messageMapper); // Ensure messageMapper is not called
  }

  @Test
  void findAllMessagesInChatOrdered_shouldReturnMessagesPage_whenMessagesFound() {
    Long chatId = 1L;
    Page<Message> messagePage = new PageImpl<>(List.of(message), pageable, 1);
    when(messageRepository.findAllByChatId(chatId, pageable)).thenReturn(messagePage);
    when(messageMapper.toMessageDtoBasic(message)).thenReturn(messageDtoBasic);

    Page<MessageDtoBasic> result = underTest.findAllMessagesInChatOrdered(chatId, pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals(messageDtoBasic, result.getContent().get(0));
    verify(messageRepository, times(1)).findAllByChatId(chatId, pageable);
    verify(messageMapper, times(1)).toMessageDtoBasic(message);
  }
}
