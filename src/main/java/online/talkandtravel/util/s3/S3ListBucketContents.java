package online.talkandtravel.util.s3;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

public class S3ListBucketContents {

  public static void main(String[] args) {

    // Create an S3 client
    S3Client s3 = S3Client.builder()
        .region(Region.of("eu-north-1")) // Specify your region
        .credentialsProvider(ProfileCredentialsProvider.create())
        .build();

    // Define your bucket name
    String bucketName = "t2-chat";

    // Create a request to list objects
    ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
        .bucket(bucketName)
        .build();

    // Get the list of objects
    ListObjectsV2Response listObjectsResponse = s3.listObjectsV2(listObjectsRequest);

    // Print the object names (keys)
    for (S3Object s3Object : listObjectsResponse.contents()) {
      System.out.println("Object Key: " + s3Object.key() + " | Size: " + s3Object.size() + " bytes");
    }

    // Close the S3 client
    s3.close();
  }
}

