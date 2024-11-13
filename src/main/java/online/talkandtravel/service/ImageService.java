package online.talkandtravel.service;

/**
 * Service interface for image generation tasks.
 *
 * <p>This service provides functionality to generate images based on input data.
 *
 * <p>Methods:
 *
 * <ul>
 *   <li>{@link #generateImage(String)} - Generates an image containing a visual representation
 *       based on the provided input. The image is created as a byte array which can be used for
 *       various purposes such as storing or displaying the image.
 * </ul>
 */
public interface ImageService {

  byte[] generateThumbnail(byte[] image, String contentType, int width);

}
