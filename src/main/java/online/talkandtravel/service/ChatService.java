package online.talkandtravel.service;

import java.util.List;
import online.talkandtravel.model.dto.chat.ChatDto;
import online.talkandtravel.model.dto.chat.ChatInfoDto;
import online.talkandtravel.model.dto.chat.NewPrivateChatDto;
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
 *   <li>{@link #findAllChats(Pageable)} - Retrieves a paginated list of all chats. The method
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


  Long createPrivateChat(NewPrivateChatDto dto);

  Page<ChatInfoDto> findAllChats(Pageable pageable);

  ChatDto findMainChat(String countryName);

  Long countUsersInChat(Long chatId);

  List<ChatInfoDto> findUserChats(Long userId);

  List<UserDtoBasic> findAllUsersByChatId(Long chatId);

  Page<MessageDtoBasic> findAllMessagesInChatOrdered(Long chatId, Pageable pageable);
}
