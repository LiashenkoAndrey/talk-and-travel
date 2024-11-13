package online.talkandtravel.service.impl;

import static online.talkandtravel.util.constants.FileFormat.GIF;
import static online.talkandtravel.util.constants.FileFormat.SVG;
import static online.talkandtravel.util.constants.FileFormat.WEBP;
import static online.talkandtravel.util.constants.FileFormatConstants.ANIMATED_WEBP_IMAGE_MARKER;

import com.luciad.imageio.webp.WebPWriteParam;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.madgag.gif.fmsware.GifDecoder;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import online.talkandtravel.exception.file.ImageProcessingException;
import online.talkandtravel.exception.file.ImageWriteException;
import online.talkandtravel.service.ImageService;
import online.talkandtravel.util.FilesUtils;
import online.talkandtravel.util.constants.FileFormat;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.springframework.stereotype.Service;


/**
 * Implementation of the {@link ImageService} for generating and processing user avatars.
 *
 * <p>This service provides methods for creating thumbnails and processing images in various formats.
 * It supports resizing images, converting them to WebP format, and handling specific image types like GIF and SVG.
 * Additionally, the service processes animated WebP images and converts them into standard WebP images with proper resizing.</p>
 *
 * <p>The service includes the following functionalities:</p>
 * <ul>
 *   <li>Resizing GIF images while preserving animation frames.</li>
 *   <li>Handling SVG images by returning them as-is without conversion.</li>
 *   <li>Resizing PNG, JPEG, and WebP images to fit within a specified size while maintaining aspect ratio.</li>
 *   <li>Converting images to WebP format for reduced file size and faster loading times.</li>
 *   <li>Detecting and processing animated WebP images.</li>
 * </ul>
 *
 * <p>Exception Handling:</p>
 * <ul>
 *   <li>{@link ImageProcessingException} - Thrown if there is an error in processing the image (e.g., unsupported format).</li>
 *   <li>{@link ImageWriteException} - Thrown if there is an error writing the image to the desired format.</li>
 * </ul>
 *
 * @see ImageService
 * @see ImageProcessingException
 * @see ImageWriteException
 */
@Service
@Log4j2
public class ImageServiceImpl implements ImageService {

  private static final int BIG_IMAGE_WIDTH = 250;


  /**
   * Generates a thumbnail of the uploaded image with a specified width.
   *
   * <p>This method handles various image formats such as GIF, SVG, and WebP, resizing them
   * appropriately while maintaining their aspect ratio. It also supports detecting and processing
   * animated WebP images, which are not supported for resizing.</p>
   *
   * @param width The target width for the thumbnail.
   * @return A byte array representing the generated thumbnail.
   * @throws ImageProcessingException If the image cannot be processed (e.g., unsupported format or error during processing).
   */
  @Override
  public byte[] generateThumbnail(byte[] image, String contentType, int width) {
    log.info("Generate thumbnail with width: {}", width);
    try {
      FileFormat fileFormat = FileFormat.fromMimeType(Objects.requireNonNull(contentType));
      log.info("Uploaded file format: {}", fileFormat);

      if (fileFormat.equals(GIF)) {
        return resizeGif(image, width);

      } else if (fileFormat.equals(SVG)) {
        log.info("Image is svg, just return bytes");
        return image;

      } else if (fileFormat.equals(WEBP) && isAnimatedWebPImage(image)) {
        log.error("File is animated webp. This format is not supported");
        throw new ImageProcessingException("Animated webp is not supported.");

      } else {
        return handleStandardImageFile(fileFormat, image, width);
      }

    } catch (Exception e) {
      log.error("Can't generate a thumbnail: {}", e.getMessage(), e);
      throw new ImageProcessingException(e.getMessage(), "Your file is invalid");
    }
  }

  /**
   * Handles standard image files (e.g., PNG, JPEG, WEBP) by resizing and converting them to WebP format.
   *
   * @param fileFormat The file format of the image.
   * @param width The target width for resizing.
   * @return A byte array representing the resized and converted image in WebP format.
   * @throws IOException If there is an error reading or writing the image data.
   */
  public byte[] handleStandardImageFile(FileFormat fileFormat, byte[] imageBytes, int width)
      throws IOException {
    log.info("Image type is PNG, JPEG, or WEBP");

    BufferedImage image = resizeImage(imageBytes, width);
    if (fileFormat.equals(WEBP)) {
      log.info("Image format is webp. No converting needed, just return.");
      return imageTyBytes(image);
    }
    return convertImageToWebpFormat(image);
  }

  public byte[] imageTyBytes(BufferedImage image) throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(image, "webp", byteArrayOutputStream);
    return byteArrayOutputStream.toByteArray();
  }

  /**
   * Resizes a GIF image while preserving its animation frames.
   *
   * <p>This method extracts the frames of the GIF, resizes each frame to fit the target size, and
   * returns the resized GIF as a byte array. It also adjusts the delay for each frame to ensure
   * proper playback speed.</p>
   *
   * @param bytes The byte array representing the original GIF image.
   * @param targetSize The target size for resizing the GIF.
   * @return A byte array representing the resized GIF image.
   */
  public byte[] resizeGif(byte[] bytes, int targetSize) {
    log.info("Resize GIF.");

    GifDecoder decoder = new GifDecoder();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    decoder.read(new ByteArrayInputStream(bytes));

    Method quality = determineNewImageQuality(decoder.getImage());
    List<Integer> framesDelays = extractFramesDelays(decoder);
    List<BufferedImage> resizedFrames = extractAndResizeFrames(decoder, targetSize, quality);

    encodeFrames(byteArrayOutputStream, resizedFrames, framesDelays);
    return byteArrayOutputStream.toByteArray();
  }

  /**
   * Determines the quality for resizing the GIF based on its dimensions.
   *
   * @param image The GIF image.
   * @return The resizing method (either {@link Method#SPEED} or {@link Method#ULTRA_QUALITY}).
   */
  private Method determineNewImageQuality(BufferedImage image) {
      if (image.getWidth() > BIG_IMAGE_WIDTH || image.getHeight() > BIG_IMAGE_WIDTH) {
        return Method.SPEED;
      }
      return Method.ULTRA_QUALITY;
  }


  /**
   * Extracts the frame delays from the decoded GIF image.
   *
   * @param decoder The GIF decoder.
   * @return A list of frame delays in milliseconds.
   */
  private List<Integer> extractFramesDelays(GifDecoder decoder) {
    List<Integer> framesDelays = new ArrayList<>();
    for (int i = 0; i < decoder.getFrameCount(); i++) {
      int delay = decoder.getDelay(i);
      framesDelays.add(delay * 2);
    }
    return framesDelays;
  }

  /**
   * Extracts and resizes each frame of the GIF image.
   *
   * @param decoder The GIF decoder.
   * @param targetSize The target size for the resized frames.
   * @param quality The desired quality for resizing the frames.
   * @return A list of resized frames as {@link BufferedImage} objects.
   */
  private List<BufferedImage> extractAndResizeFrames(GifDecoder decoder, int targetSize, Method quality) {
    List<BufferedImage> resizedFrames = new java.util.ArrayList<>();
    for (int i = 0; i < decoder.getFrameCount(); i++) {
      BufferedImage frame = decoder.getFrame(i);
      ImageDimensions imageDimensions = new ImageDimensions(frame, targetSize);
      int newWidth = imageDimensions.getNewWidth();
      int newHeight = imageDimensions.getNewHeight();
      int offsetX = Math.max(0, (newWidth - targetSize) / 2);
      int offsetY = Math.max(0, (newHeight - targetSize) / 2);
      BufferedImage resizedFrame = Scalr.resize(frame, quality, newWidth, newHeight);

      int validWidth = Math.min(targetSize, newWidth - offsetX);
      int validHeight = Math.min(targetSize, newHeight - offsetY);

      if (validWidth > 0 && validHeight > 0 && offsetX + validWidth <= resizedFrame.getWidth() && offsetY + validHeight <= resizedFrame.getHeight()) {
        resizedFrames.add(resizedFrame.getSubimage(offsetX, offsetY, validWidth, validHeight));
      } else {
        resizedFrames.add(resizedFrame);
      }
    }
    return resizedFrames;
  }

  /**
   * Encodes the resized GIF frames into a single GIF image and writes it to the output stream.
   *
   * @param byteArrayOutputStream The output stream to write the encoded GIF data.
   * @param frames The resized frames to be included in the GIF.
   * @param framesDelays The list of delays for each frame in the GIF.
   */
  private void encodeFrames(ByteArrayOutputStream byteArrayOutputStream, List<BufferedImage> frames, List<Integer> framesDelays) {
    AnimatedGifEncoder encoder = new AnimatedGifEncoder();
    encoder.start(byteArrayOutputStream);
    encoder.setRepeat(0);

    for (int i = 0; i < frames.size(); i++) {
      int delay = framesDelays.get(i);
      BufferedImage resizedFrame = frames.get(i);
      encoder.addFrame(resizedFrame);
      encoder.setDelay(delay);
    }
    encoder.finish();
  }

  /**
   * Resizes a standard image (PNG, JPEG, or WebP) to fit within the target size.
   *
   * @param targetSize The target size for the resized image.
   * @return A resized image as a {@link BufferedImage}.
   * @throws IOException If there is an error reading or writing the image data.
   */
  public BufferedImage resizeImage(byte[] image, int targetSize) throws IOException {
    ByteArrayInputStream inputStream = new ByteArrayInputStream(image);
    BufferedImage originalImage = ImageIO.read(inputStream);
    ImageDimensions imageDimensions = new ImageDimensions(originalImage, targetSize);
    int newWidth = imageDimensions.getNewWidth();
    int newHeight = imageDimensions.getNewHeight();
    Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

    BufferedImage resizedImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_ARGB);

    Graphics2D g2d = resizedImage.createGraphics();
    g2d.drawImage(scaledImage, (targetSize - newWidth) / 2, (targetSize - newHeight) / 2, null);
    g2d.dispose();

    return resizedImage;
  }

  public boolean isAnimatedWebPImage(byte[] image) {
    return FilesUtils.isAnimatedWebPImage(image);
  }

  public byte[] convertImageToWebpFormat(BufferedImage image) throws IOException {
    log.info("Convert image to webp format.");
    Iterator<ImageWriter> imageWriterIterator = ImageIO.getImageWritersBySuffix("webp");
    ImageWriter writer;
    if (imageWriterIterator.hasNext()) {
      writer = imageWriterIterator.next();
    } else {
      throw new ImageProcessingException("no writer");
    }

    WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
    writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    writeParam.setCompressionType(
        writeParam.getCompressionTypes()[WebPWriteParam.LOSSLESS_COMPRESSION]);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageOutputStream ios = ImageIO.createImageOutputStream(baos);

    writer.setOutput(ios);
    writer.write(null, new IIOImage(image, null, null), writeParam);
    ios.flush();
    ios.close();

    return baos.toByteArray();
  }

  @Getter
  private static class ImageDimensions {
    private int newWidth;
    private int newHeight;

    public ImageDimensions(BufferedImage originalImage, int targetSize) {
      this.calculateHeightWithAspectRatio(originalImage, targetSize);
    }

    public void calculateHeightWithAspectRatio(BufferedImage originalImage, int targetSize) {
      double aspectRatio = (double) originalImage.getWidth() / originalImage.getHeight();
      newWidth = targetSize;
      newHeight = (int) (targetSize / aspectRatio);

      if (newHeight < targetSize) {
        newHeight = targetSize;
        newWidth = (int) (targetSize * aspectRatio);
      }
    }
  }
}
