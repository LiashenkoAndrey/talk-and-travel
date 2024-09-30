package online.talkandtravel.model.entity.attachment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("IMAGE")
public class Image extends Attachment {

  private String thumbnailUrl; // Thumbnail for image previews
}
