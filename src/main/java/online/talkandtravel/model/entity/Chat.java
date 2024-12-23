package online.talkandtravel.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entity representing a chat within the application. This entity maps to the `chats` table and
 * includes the following fields:
 *
 * <ul>
 *   <li>{@code id} - Unique identifier for the chat, auto-generated.
 *   <li>{@code name} - Name of the chat.
 *   <li>{@code description} - Description of the chat.
 *   <li>{@code chatType} - Type of the chat, defined by the {@link ChatType} enum.
 *   <li>{@code creationDate} - Timestamp of when the chat was created, automatically managed.
 *   <li>{@code users} - List of users participating in the chat, managed through a many-to-many
 *       relationship with the `user_chats` join table.
 *   <li>{@code messages} - List of messages associated with the chat, with cascading operations and
 *       orphan removal.
 *   <li>{@code events} - List of events related to the chat, managed with a one-to-many
 *       relationship.
 *   <li>{@code country} - The country associated with the chat, mapped with a many-to-one
 *       relationship.
 * </ul>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@EqualsAndHashCode
@Table(name = "chats")
public class Chat {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;

  @Enumerated(EnumType.STRING)
  private ChatType chatType;

  @CreationTimestamp private ZonedDateTime creationDate;

  @Builder.Default
  @ManyToMany
  @JoinTable(
      name = "user_chats",
      joinColumns = @JoinColumn(name = "chat_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private List<User> users = new ArrayList<>();

  @Builder.Default
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "chat_id")
  private List<Message> messages = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "country_id")
  private Country country;

  @Override
  public String toString() {
    return "Chat{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", chatType=" + chatType +
        ", creationDate=" + creationDate +
        ", users=" + users.size() +
        ", messages=" + messages.size() +
        ", country=" + country.getName() +
        '}';
  }
}
