package online.talkandtravel.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chats")
public class Chat {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;

  @Enumerated(EnumType.STRING)
  private ChatType chatType;

  private LocalDateTime creationDate;

  @ManyToMany
  @JoinTable(
      name = "chat_user",
      joinColumns = @JoinColumn(name = "chat_id"),
      inverseJoinColumns = @JoinColumn(name = "user_id")
  )
  private List<User> users;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "chat_id")
  private List<Message> messages;

  @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Event> events;
}

