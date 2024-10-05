package online.talkandtravel.model.entity.attachment;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("VIDEO")
public class Video extends Attachment {

  private String thumbnailUrl; // Thumbnail for video previews

  private Long duration; // Duration of the video in seconds
}
