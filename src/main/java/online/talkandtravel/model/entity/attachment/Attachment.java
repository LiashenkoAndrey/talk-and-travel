package online.talkandtravel.model.entity.attachment;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "attachments")
public abstract class Attachment {

  @Id
  private String id;

  private String fileName;
//  private long size; // File size in bytes
//  private String mimeType; // MIME type (e.g., image/jpeg, video/mp4)
}
