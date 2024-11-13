package online.talkandtravel.model.entity.attachment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity
@ToString(callSuper = true)
@DiscriminatorValue("IMAGE")
public class Image extends Attachment {

  @Builder
  public Image(String id, String fileName) {
    super(id, fileName);
  }


}
