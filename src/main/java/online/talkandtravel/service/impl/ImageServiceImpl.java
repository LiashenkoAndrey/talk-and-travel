package online.talkandtravel.service.impl;

import online.talkandtravel.exception.file.ImageWriteException;
import online.talkandtravel.service.ImageService;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ImageServiceImpl implements ImageService {
    private static final int INDEX = 0;
    private static final int IMAGE_X = 0;
    private static final int IMAGE_Y = 0;
    private static final int IMAGE_WIDTH = 200;
    private static final int IMAGE_HEIGHT = 200;
    private static final int OVAL_WIDTH = 100;
    private static final int OVAL_HEIGHT = 100;
    private static final int OVAL_X = 50;
    private static final int OVAL_Y = 50;
    private static final String FONT_NAME = "Arial";
    private static final int FONT_SIZE = 50;

    @Override
    public byte[] generateImage(String name) {
        log.info("generateImage: name - {}", name);
        char firstLetterOfName = name.charAt(INDEX);
        var image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        createImageWithFirstLetter(firstLetterOfName, image);
        var byteArrayOutputStream = new ByteArrayOutputStream();
        writeImageToPngFormat(image, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void writeImageToPngFormat(BufferedImage image, ByteArrayOutputStream byteArrayOutputStream) {
        try {
            ImageIO.write(image, "png", byteArrayOutputStream);
        } catch (IOException e) {
            throw new ImageWriteException("Error writing an image to PNG format");
        }
    }

    private void createImageWithFirstLetter(char firstLetterOfName, BufferedImage image) {
        log.info("calculateXAndYCoordinates: firstLetterOfName - {}", firstLetterOfName);
        Graphics2D graphics = getGraphics2D(image);
        int[] xAndYCoordinatesOfFirstLetterOfName
                = calculateXAndYCoordinates(graphics, firstLetterOfName);
        int xCoordinateOfLatter = xAndYCoordinatesOfFirstLetterOfName[0];
        int yCoordinateOfLatter = xAndYCoordinatesOfFirstLetterOfName[1];
        graphics.drawString(
                String.valueOf(firstLetterOfName), xCoordinateOfLatter, yCoordinateOfLatter
        );
        graphics.dispose();
    }

    private Graphics2D getGraphics2D(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(IMAGE_X, IMAGE_Y, IMAGE_WIDTH, IMAGE_HEIGHT);
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillOval(OVAL_X, OVAL_Y, OVAL_WIDTH, OVAL_HEIGHT);
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
        return graphics;
    }

    private int[] calculateXAndYCoordinates(Graphics2D graphics, char letter) {
        log.info("calculateXAndYCoordinates: letter - {}, graphics - {}", letter, graphics);
        int[] xAndYCoordinates = new int[2];
        FontMetrics fontMetrics = graphics.getFontMetrics();
        xAndYCoordinates[0] = (IMAGE_WIDTH - fontMetrics.charWidth(letter)) / 2;
        xAndYCoordinates[1] = (IMAGE_HEIGHT + fontMetrics.getAscent() - fontMetrics.getDescent()) / 2;
        return xAndYCoordinates;
    }
}
