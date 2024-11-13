package online.talkandtravel.service;

import online.talkandtravel.model.entity.attachment.AttachmentType;
import online.talkandtravel.model.entity.attachment.Image;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {

  void validateAttachmentFile(MultipartFile file, String attachmentType);

  void saveImage(byte[] image, String folderName, String contentType, String key);

  String generateImageUrl(Image image, String avatarDimension);
}
