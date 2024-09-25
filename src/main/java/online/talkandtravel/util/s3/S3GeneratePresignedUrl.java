package online.talkandtravel.util.s3;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import java.time.Duration;

public class S3GeneratePresignedUrl {

  public static void main(String[] args) {

    // Create an S3 presigner client
    S3Presigner presigner = S3Presigner.builder()
        .region(Region.of("eu-north-1")) // Replace with your region
        .credentialsProvider(ProfileCredentialsProvider.create())
        .build();

    String bucketName = "t2-chat";
    String objectKey = "chat_123/originals/c9ca068b-478f-4dd3-b5ef-808e21949116.jpg";

    // Create the GetObjectRequest
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(objectKey)
        .build();

    // Generate a presigned URL valid for 60 minutes
    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(60))  // URL expires after 60 minutes
        .getObjectRequest(getObjectRequest)
        .build();

    String presignedUrl = presigner.presignGetObject(presignRequest).url().toString();
    System.out.println("Presigned URL: " + presignedUrl);

    // Close the presigner
    presigner.close();
  }
}

