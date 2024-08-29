package online.talkandtravel.service;

import java.util.List;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.dto.chat.ChatInfoDto;
import online.talkandtravel.model.dto.chat.NewChatDto;
import online.talkandtravel.model.dto.chat.NewPrivateChatDto;
import online.talkandtravel.model.dto.chat.PrivateChatDto;
import online.talkandtravel.model.dto.chat.PrivateChatInfoDto;
import online.talkandtravel.model.dto.chat.SetLastReadMessageRequest;
import online.talkandtravel.model.dto.message.MessageDtoBasic;
import online.talkandtravel.model.dto.user.UserDtoBasic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing chat operations.
 *
 * <p>This service provides methods to interact with chat-related data, including finding chats,
 * retrieving chat details, counting users, and fetching messages and users within chats.
 *
 * <p>Methods:
 *
 * <ul>
 *   <li>{@link #createPrivateChat(NewPrivateChatDto)} - creates private chat between two users
 *   <li>{@link #findAllUsersPrivateChats(Long)} - finds all private chats of a user
 *   <li>{@link #setLastReadMessage(Long, SetLastReadMessageRequest)} - updates lastReadMessage of
 *       field that represents last read message of chat by user
 *   <li>{@link #findReadMessages(Long, Pageable)} - finds messages that the user has already read
 *   <li>{@link #findUnreadMessages(Long, Pageable)} - finds messages that the user has not yet read
 *   <li>{@link #findAllGroupChats(Pageable)} - Retrieves a paginated list of all chats. The method
 *       returns a {@link Page} of {@link ChatDto} objects, allowing for efficient querying and
 *       pagination.
 *   <li>{@link #findMainChat(String)} - Finds the main chat for a given country. This method
 *       returns a {@link ChatDto} representing the main chat associated with the specified country
 *       name.
 *   <li>{@link #countUsersInChat(Long)} - Counts the number of users in a specific chat. The method
 *       returns the total number of users associated with the chat identified by the provided chat
 *       ID.
 *   <li>{@link #findUserChats(Long)} - Retrieves a list of chats associated with a specific user.
 *       This method returns a list of {@link ChatDto} objects representing all chats that the user
 *       with the specified user ID is a part of.
 *   <li>{@link #findAllUsersByChatId(Long)} - Finds all users participating in a specified chat.
 *       The method returns a list of {@link UserDtoBasic} objects for users associated with the
 *       given chat ID.
 *   <li>{@link #findAllMessagesInChatOrdered(Long, Pageable)} - Retrieves a paginated list of all
 *       messages within a specific chat, ordered according to the defined sorting criteria. The
 *       method returns a {@link Page} of {@link MessageDtoBasic} objects, representing messages in
 *       the chat identified by the provided chat ID.
 * </ul>
 */
public interface ChatService {

  ChatDto createCountryChat(NewChatDto dto);

  Long createPrivateChat(NewPrivateChatDto dto);

  List<PrivateChatDto> findAllUsersPrivateChats(Long userId);

  void setLastReadMessage(Long chatId, SetLastReadMessageRequest dtoRequest);

  Page<MessageDtoBasic> findReadMessages(Long chatId, Pageable pageable);

  Page<MessageDtoBasic> findUnreadMessages(Long chatId, Pageable pageable);

  Page<ChatInfoDto> findAllGroupChats(Pageable pageable);

  ChatDto findMainChat(String countryName);

  Long countUsersInChat(Long chatId);

  List<PrivateChatInfoDto> findUserChats(Long userId);

  List<UserDtoBasic> findAllUsersByChatId(Long chatId);

  Page<MessageDtoBasic> findAllMessagesInChatOrdered(Long chatId, Pageable pageable);

  ChatDto findChatById(Long chatId);
}
