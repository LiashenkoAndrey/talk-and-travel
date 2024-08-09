package online.talkandtravel.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Table(name = "chats")
public class Chat {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;

  @Enumerated(EnumType.STRING)
  private ChatType chatType;

  @CreationTimestamp private LocalDateTime creationDate;

  @Builder.Default
  @ManyToMany
  @JoinTable(
      name = "user_chats",
      joinColumns = @JoinColumn(name = "chat_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id"))
  private List<UserChat> users = new ArrayList<>();

  @Builder.Default
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "chat_id")
  private List<Message> messages = new ArrayList<>();

  @Builder.Default
  @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Event> events = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "country_id")
  private Country country;
}
