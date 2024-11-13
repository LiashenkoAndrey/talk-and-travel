package online.talkandtravel.service;

import online.talkandtravel.model.entity.attachment.Image;

public interface AttachmentService {

  void saveImage(byte[] image, String folderName, String contentType, String key);

  String generateImageUrl(Image image, String avatarDimension);
}
