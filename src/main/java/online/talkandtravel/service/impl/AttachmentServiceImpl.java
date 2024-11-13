package online.talkandtravel.service.impl;

import static online.talkandtravel.util.constants.S3Constants.S3_URL_PATTERN;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.model.entity.Avatar;
import online.talkandtravel.model.entity.attachment.Image;
import online.talkandtravel.service.AttachmentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
  private String S3_FOLDER_NAME;

  @Value("${aws.region}")
  private String AWS_REGION;

  @Value("${aws.s3.avatarsFolderName}")
  private String AWS_S3_AVATARS_FOLDER_NAME;

  private final S3Client s3Client;

  @Override
  public void saveImage(byte[] image, String imageFolderName, String contentType, String key){
    String imagePath = S3_FOLDER_NAME + imageFolderName + "/" + key;
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
    String fullAvatarFolderPath = AWS_S3_AVATARS_FOLDER_NAME + avatarS3Folder;
    return S3_URL_PATTERN.formatted(AWS_S3_BUCKET_NAME, AWS_REGION, fullAvatarFolderPath,
        image.getId());
  }

}
