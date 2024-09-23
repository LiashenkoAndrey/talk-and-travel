package online.talkandtravel.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entity representing a message in a chat.
 *
 * <ul>
 *   <li>{@code id} - The unique identifier for the message.
 *   <li>{@code content} - The text content of the message.
 *   <li>{@code creationDate} - The timestamp when the message was created.
 *   <li>{@code sender} - The user who sent the message.
 *   <li>{@code chat} - The chat in which the message was sent.
 *   <li>{@code repliedMessage} - The message that this message is replying to, if any.
 * </ul>
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "messages")
public class Message {

  public Message(String content) {
    this.content = content;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String content;

  @CreationTimestamp private LocalDateTime creationDate;

  @ManyToOne
  @JoinColumn(name = "sender_id")
  private User sender;

  @ManyToOne
  @JoinColumn(name = "chat_id")
  private Chat chat;

  @Enumerated(EnumType.STRING)
  private MessageType type;

  @ManyToOne
  @JoinColumn(name = "replied_message_id")
  private Message repliedMessage;
}
