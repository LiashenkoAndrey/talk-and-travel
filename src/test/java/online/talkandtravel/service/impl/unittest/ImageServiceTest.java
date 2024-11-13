package online.talkandtravel.service.impl.unittest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.file.ImageProcessingException;
import online.talkandtravel.service.impl.ImageServiceImpl;
import online.talkandtravel.util.constants.FileFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@Log4j2
public class ImageServiceTest {

  @Mock
  private MultipartFile mockFile;

  private ImageServiceImpl underTest;

  private byte[] expectedBytes;
  private int width;
  private BufferedImage mockBufferedImage;
  byte[] imageBytes;

  @BeforeEach
  void setUp() {
    imageBytes = new byte[] {61,45,5,52,54,55,65};
    underTest = spy(new ImageServiceImpl());
    expectedBytes = new byte[]{5, 5, 5};
    width = 64;
    mockBufferedImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
  }

  @Test
  void generateThumbnail_shouldReturnPngThumbnail_whenPngImage() throws IOException {
    String pngImageContentType = "image/png";
    doReturn(expectedBytes).when(underTest).handleStandardImageFile(FileFormat.PNG, imageBytes, width);

    byte[] actual = underTest.generateThumbnail(imageBytes, pngImageContentType, width);
    log.info(Arrays.toString(actual));
    assertNotNull(actual);
    assertArrayEquals(expectedBytes, actual);

    verify(underTest).handleStandardImageFile(eq(FileFormat.PNG), eq(imageBytes), eq(width));
    verify(underTest, never()).resizeGif(any(), anyInt());
    verify(mockFile, never()).getBytes();
    verify(underTest, never()).isAnimatedWebPImage(any());
    verify(underTest, never()).imageTyBytes(any(BufferedImage.class));
  }

  @Test
  void generateThumbnail_shouldReturnWebpThumbnail_whenWebpImage() throws IOException {
    String webpImageContentType = "image/webp";
    doReturn(false).when(underTest).isAnimatedWebPImage(imageBytes);
    doReturn(mockBufferedImage).when(underTest).resizeImage(imageBytes, width);
    doReturn(expectedBytes).when(underTest).imageTyBytes(mockBufferedImage);

    byte[] actual = underTest.generateThumbnail(imageBytes, webpImageContentType, width);

    assertNotNull(actual);
    assertArrayEquals(expectedBytes, actual);

    verify(underTest).handleStandardImageFile(eq(FileFormat.WEBP), eq(imageBytes), anyInt());
    verify(underTest, never()).resizeGif(any(), anyInt());
    verify(mockFile, never()).getBytes();
    verify(underTest).isAnimatedWebPImage(imageBytes);
    verify(underTest).imageTyBytes(mockBufferedImage);
    verify(underTest, never()).convertImageToWebpFormat(any(BufferedImage.class));
  }

  @Test
  void generateThumbnail_shouldReturnGifThumbnail_whenGifImage() throws IOException {
    String imageType = "image/gif";
    byte[] gifExpectedBytes = {5};
    int gifWidth = 1;

    doReturn(gifExpectedBytes).when(underTest).resizeGif(imageBytes, gifWidth);

    byte[] actual = underTest.generateThumbnail(imageBytes, imageType, gifWidth);
    log.info(Arrays.toString(actual));
    assertNotNull(actual);
    assertArrayEquals(gifExpectedBytes, actual);

    verify(underTest, never()).handleStandardImageFile(eq(FileFormat.PNG), eq(imageBytes), eq(gifWidth));
    verify(mockFile, never()).getInputStream();
    verify(underTest, never()).isAnimatedWebPImage(any());
  }

  @Test
  void generateThumbnail_shouldReturnSvgThumbnail_whenSvgImage() throws IOException {
    String imageType = "image/svg+xml";
    int svgWidth = 1;

    byte[] actual = underTest.generateThumbnail(imageBytes, imageType, svgWidth);
    assertNotNull(actual);
    assertArrayEquals(imageBytes, actual);

    verify(underTest, never()).handleStandardImageFile(eq(FileFormat.PNG), eq(imageBytes), eq(svgWidth));
    verify(underTest, never()).resizeGif(any(), anyInt());
    verify(mockFile, never()).getInputStream();
    verify(underTest, never()).isAnimatedWebPImage(any());
  }

  @Test
  void generateThumbnail_shouldThrowExceptionWhenAnimatedWebp() throws IOException {
    String imageType = "image/webp";

    doReturn(true).when(underTest).isAnimatedWebPImage(imageBytes);

    assertThrows(ImageProcessingException.class, () -> {
      underTest.generateThumbnail(imageBytes, imageType, width);
    }, "Animated webp is not supported.");

    verify(underTest, never()).handleStandardImageFile(eq(FileFormat.PNG), eq(imageBytes), eq(width));
    verify(underTest, never()).resizeGif(any(), anyInt());
    verify(underTest).isAnimatedWebPImage(imageBytes);
  }

}
