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
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

  private InputStream mockInputStream;
  private byte[] expectedBytes;
  private int width;
  private BufferedImage mockBufferedImage;

  @BeforeEach
  void setUp() {
    underTest = spy(new ImageServiceImpl());
    mockInputStream = new ByteArrayInputStream(new byte[]{1, 2, 3});
    expectedBytes = new byte[]{5, 5, 5};
    width = 64;
    mockBufferedImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
  }

  @Test
  void generateThumbnail_shouldReturnPngThumbnail_whenPngImage() throws IOException {
    when(mockFile.getContentType()).thenReturn("image/png");
    when(mockFile.getInputStream()).thenReturn(mockInputStream);
    doReturn(expectedBytes).when(underTest).handleStandardImageFile(FileFormat.PNG, mockInputStream, width);

    byte[] actual = underTest.generateThumbnail(mockFile, width);
    log.info(Arrays.toString(actual));
    assertNotNull(actual);
    assertArrayEquals(expectedBytes, actual);

    verify(underTest).handleStandardImageFile(eq(FileFormat.PNG), any(InputStream.class), eq(width));
    verify(underTest, never()).resizeGif(any(), anyInt());
    verify(mockFile, never()).getBytes();
    verify(underTest, never()).isAnimatedWebP(any());
    verify(underTest, never()).imageTyBytes(any(BufferedImage.class));
  }

  @Test
  void generateThumbnail_shouldReturnWebpThumbnail_whenWebpImage() throws IOException {
    when(mockFile.getContentType()).thenReturn("image/webp");
    when(mockFile.getInputStream()).thenReturn(mockInputStream);
    doReturn(false).when(underTest).isAnimatedWebP(mockInputStream);
    doReturn(mockBufferedImage).when(underTest).resizeImage(mockInputStream, width);
    doReturn(expectedBytes).when(underTest).imageTyBytes(mockBufferedImage);

    byte[] actual = underTest.generateThumbnail(mockFile, width);

    assertNotNull(actual);
    assertArrayEquals(expectedBytes, actual);

    verify(underTest).handleStandardImageFile(eq(FileFormat.WEBP), any(InputStream.class), anyInt());
    verify(underTest, never()).resizeGif(any(), anyInt());
    verify(mockFile, never()).getBytes();
    verify(underTest).isAnimatedWebP(mockInputStream);
    verify(underTest).imageTyBytes(mockBufferedImage);
    verify(underTest, never()).convertImageToWebpFormat(any(BufferedImage.class));
  }

  @Test
  void generateThumbnail_shouldReturnGifThumbnail_whenGifImage() throws IOException {
    byte[] inputImage = {5,34};
    byte[] gifExpectedBytes = {5};
    int gifWidth = 1;

    when(mockFile.getContentType()).thenReturn("image/gif");
    when(mockFile.getBytes()).thenReturn(inputImage);
    doReturn(gifExpectedBytes).when(underTest).resizeGif(inputImage, gifWidth);

    byte[] actual = underTest.generateThumbnail(mockFile, gifWidth);
    log.info(Arrays.toString(actual));
    assertNotNull(actual);
    assertArrayEquals(gifExpectedBytes, actual);

    verify(underTest, never()).handleStandardImageFile(eq(FileFormat.PNG), any(InputStream.class), eq(gifWidth));
    verify(mockFile, never()).getInputStream();
    verify(underTest, never()).isAnimatedWebP(any());
  }

  @Test
  void generateThumbnail_shouldReturnSvgThumbnail_whenSvgImage() throws IOException {
    byte[] svgExpectedBytes = {5};
    int svgWidth = 1;

    when(mockFile.getContentType()).thenReturn("image/svg+xml");
    when(mockFile.getBytes()).thenReturn(svgExpectedBytes);

    byte[] actual = underTest.generateThumbnail(mockFile, svgWidth);
    log.info(Arrays.toString(actual));
    assertNotNull(actual);
    assertArrayEquals(svgExpectedBytes, actual);

    verify(underTest, never()).handleStandardImageFile(eq(FileFormat.PNG), any(InputStream.class), eq(svgWidth));
    verify(underTest, never()).resizeGif(any(), anyInt());
    verify(mockFile, never()).getInputStream();
    verify(mockFile).getBytes();
    verify(underTest, never()).isAnimatedWebP(any());
  }

  @Test
  void generateThumbnail_shouldThrowExceptionWhenAnimatedWebp() throws IOException {
    when(mockFile.getContentType()).thenReturn("image/webp");
    when(mockFile.getInputStream()).thenReturn(mockInputStream);
    doReturn(true).when(underTest).isAnimatedWebP(mockInputStream);

    assertThrows(ImageProcessingException.class, () -> {
      underTest.generateThumbnail(mockFile, width);
    }, "Animated webp is not supported.");

    verify(underTest, never()).handleStandardImageFile(eq(FileFormat.PNG), any(InputStream.class), eq(width));
    verify(underTest, never()).resizeGif(any(), anyInt());
    verify(mockFile, never()).getBytes();
    verify(mockFile).getInputStream();
    verify(underTest).isAnimatedWebP(mockInputStream);
  }

}
