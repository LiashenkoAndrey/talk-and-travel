package online.talkandtravel.service.impl;

import static online.talkandtravel.util.FilesUtils.bytesToMegabytes;
import static online.talkandtravel.util.FilesUtils.getBytes;
import static online.talkandtravel.util.FilesUtils.isAnimatedWebPImage;
import static online.talkandtravel.util.constants.AttachmentConstants.SUPPORTED_IMAGE_ATTACHMENT_FORMATS;
import static online.talkandtravel.util.constants.S3Constants.S3_URL_PATTERN;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.attachment.UnsupportedAttachmentTypeException;
import online.talkandtravel.exception.file.FileSizeExceededException;
import online.talkandtravel.exception.file.ImageProcessingException;
import online.talkandtravel.exception.file.UnsupportedImageFormatException;
import online.talkandtravel.model.entity.attachment.AttachmentType;
import online.talkandtravel.model.entity.attachment.Image;
import online.talkandtravel.service.AttachmentService;
import online.talkandtravel.util.FileDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@RequiredArgsConstructor
@Log4j2
@Service
public class AttachmentServiceImpl implements AttachmentService {

  @Value("${aws.s3.bucketName}")
  private String AWS_S3_BUCKET_NAME;

  @Value("${aws.s3.attachmentsFolderName}")
  private String AWS_S3_FOLDER_NAME;

  @Value("${aws.region}")
  private String AWS_REGION;

  @Value("${aws.s3.attachmentsFolderName}")
  private String AWS_S3_ATTACHMENTS_FOLDER_NAME;

  @Value("${attachments.images.maxSize}")
  private Long IMAGES_MAX_SIZE;

  private final S3Client s3Client;

  @Override
  public void validateAttachmentFile(MultipartFile file, AttachmentType attachmentType) {
    validateAttachmentType(attachmentType);
    if (attachmentType.equals(AttachmentType.IMAGE)) {
      validateImage(file);
    }
  }

  public void validateImage(MultipartFile file) {
    validateImageSize(file.getSize());
    validateImageFileFormat(file.getContentType());
    if (isAnimatedWebPImage(getBytes(file))) {
      throw new ImageProcessingException("Animated webp is not supported.");
    }
  }

  public void validateAttachmentType(AttachmentType attachmentType) {
    if (!Arrays.asList(AttachmentType.values()).contains(attachmentType)) {
      throw new UnsupportedAttachmentTypeException(attachmentType);
    }
  }

  @Override
  public void saveImage(byte[] image, String imageFolderName, String contentType, String key){
    String imagePath = AWS_S3_FOLDER_NAME + imageFolderName + "/" + key;
    log.info("save image to path: {}", imagePath);

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(AWS_S3_BUCKET_NAME)
        .key(imagePath)
        .contentType(contentType)
        .build();

    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(image));
  }

  @Override
  public String generateImageUrl(Image image, String avatarS3Folder) {
    String fullAvatarFolderPath = AWS_S3_ATTACHMENTS_FOLDER_NAME + avatarS3Folder;
    return S3_URL_PATTERN.formatted(AWS_S3_BUCKET_NAME, AWS_REGION, fullAvatarFolderPath,
        image.getId());
  }

  private void validateImageFileFormat(String contentType) {
    String type = MediaType.valueOf(contentType).getSubtype();
    log.info("Image type: {}", type);
    if (!Arrays.asList(SUPPORTED_IMAGE_ATTACHMENT_FORMATS).contains(type)) {
      throw new UnsupportedImageFormatException(type, SUPPORTED_IMAGE_ATTACHMENT_FORMATS);
    }
  }

  private void validateImageSize(Long size) {
    if (bytesToMegabytes(size) > IMAGES_MAX_SIZE) {
      throw new FileSizeExceededException(size.intValue());
    }
  }
}
