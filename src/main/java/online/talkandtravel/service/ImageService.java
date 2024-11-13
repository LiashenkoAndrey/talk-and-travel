package online.talkandtravel.service;

/**
 * Service interface for image generation tasks.
 *
 * <p>This service provides functionality to generate images based on input data.
 */
public interface ImageService {

  byte[] generateThumbnail(byte[] image, String contentType, int width);

}
