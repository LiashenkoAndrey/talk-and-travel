package online.talkandtravel.util.constants;

import lombok.Getter;

@Getter
public enum FileFormat {
  JPEG("jpeg"),
  GIF("gif"),
  PNG("png"),
  TIFF("tiff"),
  WEBP("webp"),
  SVG("svg+xml");


  private final String extension;

  FileFormat(String extension) {
    this.extension = extension;
  }

  public static FileFormat fromMimeType(String mimeType) {
    if (mimeType != null) {
      String subtype = mimeType.split("/")[1].toLowerCase(); // Get the subtype in lowercase
      System.out.println(subtype);
      for (FileFormat format : values()) {
        if (format.getExtension().equals(subtype)) {
          return format;
        }
      }
    }
    throw new IllegalArgumentException("Unsupported MIME type: " + mimeType);
  }
}
