package online.talkandtravel.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing the relationship between a user and a chat.
 *
 * <p>This class is used to manage the association of a user with a chat, including:
 *
 * <ul>
 *   <li>{@code id} - Unique identifier for the user-chat relationship.
 *   <li>{@code user} - The user associated with the chat.
 *   <li>{@code chat} - The chat associated with the user.
 *   <li>{@code userCountry} - The user's country, if applicable.
 *   <li>{@code lastReadMessageId} - The ID of the last message read by the user in the chat.
 * </ul>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Table(name = "user_chats")
public class UserChat {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "chat_id")
  private Chat chat;

  @ManyToOne private UserCountry userCountry;

  @Override
  public String toString() {
    return "UserChat{" +
        "id=" + id +
        ", user=" + user.getId() +
        ", chat=" + chat.getId() +
        ", lastReadMessageId=" + lastReadMessageId +
        '}';
  }

  private Long lastReadMessageId;
}
