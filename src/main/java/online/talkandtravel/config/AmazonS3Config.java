package online.talkandtravel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AmazonS3Config {

  @Value("${aws.accessKeyId}")
  private String AWS_ACCESS_KEY_ID;

  @Value("${aws.secretKey}")
  private String AWS_ACCESS_KEY;

  @Value("${aws.region}")
  private String AWS_REGION;

  @Bean
  public S3Client s3Client() {
    AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
        AWS_ACCESS_KEY_ID, AWS_ACCESS_KEY);

    return S3Client.builder()
        .region(Region.of(AWS_REGION))
        .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
        .build();
  }

}
