package online.talkandtravel.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing user avatars. This entity maps to the `avatars` table and includes the
 * following fields:
 *
 * <ul>
 *   <li>{@code id} - Unique identifier for the avatar, auto-generated.
 *   <li>{@code user} - The user associated with this avatar, mapped via a one-to-one relationship.
 *   <li>{@code content} - The binary data representing the avatar image.
 * </ul>
 */
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "avatars")
public class Avatar {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Lob private byte[] content;
}
