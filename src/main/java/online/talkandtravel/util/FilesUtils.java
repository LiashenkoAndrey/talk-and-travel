package online.talkandtravel.util;

import static online.talkandtravel.util.constants.FileFormatConstants.ANIMATED_WEBP_IMAGE_MARKER;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.file.ImageProcessingException;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
public class FilesUtils {
  public static double bytesToMegabytes(long bytes) {
    return bytes / (1024.0 * 1024.0);
  }

  public static FileDto toFileDto(MultipartFile file) {
    return new FileDto(getBytes(file), file.getContentType(), file.getOriginalFilename(),
        file.getSize());
  }

  public static boolean isAnimatedWebPImage(byte[] image)  {
    try {
      ByteArrayInputStream webpStream = new ByteArrayInputStream(image);
      byte[] buffer = new byte[64];
      webpStream.read(buffer);

      String header = new String(buffer, StandardCharsets.UTF_8);
      return header.contains(ANIMATED_WEBP_IMAGE_MARKER);
    } catch (IOException e) {
      log.error("Can't read input stream");
      throw new ImageProcessingException(e.getMessage());
    }

  }

  public static byte[] getBytes(MultipartFile file) {
    try {
      return file.getBytes();
    } catch (Exception e) {
      log.info("Error when get bytes");
      throw new RuntimeException(e);
    }
  }
}
