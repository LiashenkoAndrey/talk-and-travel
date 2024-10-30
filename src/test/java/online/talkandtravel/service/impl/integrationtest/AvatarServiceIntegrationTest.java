package online.talkandtravel.service.impl.integrationtest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.adobe.testing.s3mock.testcontainers.S3MockContainer;
import java.net.URI;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.config.IntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@Log4j2
public class AvatarServiceIntegrationTest extends IntegrationTest {

  private S3Client s3Client;
  private static final String TEST_BUCKET_NAME = "test-bucket";

  @Autowired private S3MockContainer s3MockContainer;

  @BeforeEach
  public void setUp() {
    if (!s3MockContainer.isRunning()) {
      s3MockContainer.start();
    }
    s3Client = S3Client.builder()
        .endpointOverride(URI.create(s3MockContainer.getHttpEndpoint()))
        .region(Region.US_EAST_1)
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create("accessKey", "secretKey")))
        .forcePathStyle(true)
        .build();
  }

  @Test
  void test() {
    s3Client.createBucket(CreateBucketRequest.builder().bucket(TEST_BUCKET_NAME).build());
    log.info(s3Client.listBuckets().buckets());
    assertTrue(s3Client.listBuckets().hasBuckets());
    var createdBucketName = s3Client.listBuckets().buckets().get(0).name();
    assertThat(TEST_BUCKET_NAME).isEqualTo(createdBucketName);
  }

}
