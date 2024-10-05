package online.talkandtravel.model.entity.attachment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("FILE")
public class File extends Attachment {

  // No additional fields for generic files

}
