package online.talkandtravel.util.s3;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

public class S3Example {

  public static void main(String[] args) {

    // Create an S3 client using the default profile or a specific one if needed
    S3Client s3 = S3Client.builder()
        .region(Region.of("eu-north-1")) // Specify your region, e.g., "us-west-1"
        .credentialsProvider(ProfileCredentialsProvider.create("default")) // Use default AWS profile
        .build();

    try {
      // List S3 buckets
      ListBucketsResponse bucketsResponse = s3.listBuckets();
      System.out.println("Your Amazon S3 buckets are:");
      bucketsResponse.buckets().forEach(x -> System.out.println(x.name()));
    } catch (S3Exception e) {
      System.err.println(e.awsErrorDetails().errorMessage());
    } finally {
      // Close the S3 client
      s3.close();
    }
  }
}

