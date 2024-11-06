package online.talkandtravel.util.constants;

import org.springframework.http.MediaType;

public class FileFormatConstants {


  public static final String[] SUPPORTED_FORMAT_AVATAR = {"jpeg", "jpg", "png", "webp", "svg", "gif", "tiff"};
  public static final String ANIMATED_WEBP_IMAGE_MARKER = "ANIM";
  public static final MediaType IMAGE_WEBP = new MediaType("image", "webp");

}
