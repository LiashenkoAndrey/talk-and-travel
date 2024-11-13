package online.talkandtravel.util;

import online.talkandtravel.model.dto.attachment.AttachmentDto;
import online.talkandtravel.model.dto.attachment.ImageAttachmentDto;
import online.talkandtravel.model.entity.attachment.Image;
import online.talkandtravel.model.entity.attachment.Video;

public class AttachmentDtoFactory {
  public static AttachmentDto createAttachmentDto(Object attachment) {
    if (attachment instanceof Image) {
      return new ImageAttachmentDto("imageFile.jpg", "1024L", "https://imageurl.com");  // Example parameters
    } else if (attachment instanceof Video) {
      return new ImageAttachmentDto(null, null, null);
    } else {
      throw new UnsupportedOperationException("Unsupported attachment type");
    }
  }
}
