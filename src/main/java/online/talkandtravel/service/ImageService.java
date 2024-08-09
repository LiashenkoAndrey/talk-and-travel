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
 *
 * @param name The input data used to generate the image. For example, this could be a name where
 *     the image includes the first letter of the name.
 * @return A byte array representing the generated image in PNG format.
 */
public interface ImageService {

  byte[] generateImage(String name);
}
