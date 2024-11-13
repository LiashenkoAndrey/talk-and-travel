package online.talkandtravel.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.multipart.MultipartFile;

@Log4j2
public class FilesUtils {
  public static double bytesToMegabytes(long bytes) {
    return bytes / (1024.0 * 1024.0);
  }

  public static FileDto toFile(MultipartFile file) {
    return new FileDto(getBytes(file), file.getContentType(), file.getOriginalFilename(),
        file.getSize());
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
