package online.talkandtravel.model.entity.attachment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("AUDIO")
public class Audio extends Attachment {

  private Long duration; // Duration of the audio in seconds

}
