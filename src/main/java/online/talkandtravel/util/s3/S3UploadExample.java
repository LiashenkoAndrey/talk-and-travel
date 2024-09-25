package online.talkandtravel.util.s3;

import java.nio.file.Paths;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class S3UploadExample {

  public static final String REGION = "eu-north-1";

  public static void main(String[] args) {

    // Create an S3 client
    S3Client s3 =
        S3Client.builder()
            .region(Region.of(REGION)) // Specify your region
            .credentialsProvider(
                ProfileCredentialsProvider.create()) // when deploy on server, just delete this line
            .build();

    String bucketName = "t2-chat";
    String objectKey = "chat_123/originals/c9ca068b-478f-4dd3-b5ef-808e21949116.jpg";
    String filePath = "src/main/resources/c9ca068b-478f-4dd3-b5ef-808e21949116.jpg";

    // Upload a file
    s3.putObject(
        PutObjectRequest.builder().bucket(bucketName).key(objectKey).build(),
        RequestBody.fromFile(Paths.get(filePath)));

    System.out.println("File uploaded successfully!");

    // Construct the URL of the uploaded file
    String fileUrl =
        String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, REGION, objectKey);
    System.out.println("File URL: " + fileUrl);

    // Close the S3 client
    s3.close();
  }
}
