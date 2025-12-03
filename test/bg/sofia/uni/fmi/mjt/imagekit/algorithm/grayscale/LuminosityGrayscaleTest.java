package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import bg.sofia.uni.fmi.mjt.imagekit.filesystem.LocalFileSystemImageManager;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LuminosityGrayscaleTest {

    @Test
    void testNullBufferImageForGrayscale() {
        LuminosityGrayscale luminosityGrayscale = new LuminosityGrayscale();
        assertThrows(IllegalArgumentException.class, () -> luminosityGrayscale.process(null), "" +
                "When null is given to process for grayscale, illegal argument exception shall be thrown");
    }

    @Test
    void testSuccessfullyGrayscale() throws IOException {

        LuminosityGrayscale luminosityGrayscale = new LuminosityGrayscale();
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();

        File expectedGrayscaleImage = new File("swampert-grayscale.png");
        File inputGrayscaleImage = new File("Swampert.png");

        BufferedImage inputImage = localFileSystemImageManager.loadImage(inputGrayscaleImage);
        BufferedImage expectedImage = localFileSystemImageManager.loadImage(expectedGrayscaleImage);

        BufferedImage afterLuminosity = luminosityGrayscale.process(inputImage);

        assertTrue(compareBufferedImages(afterLuminosity,expectedImage),
                "The result after luminosity is not proper");
    }

    private boolean compareBufferedImages(BufferedImage bufferedOne, BufferedImage bufferedTwo) {

        if(!(bufferedOne.getWidth() == bufferedTwo.getWidth() && bufferedOne.getHeight() == bufferedTwo.getHeight())){
            return false;
        }

        int totalWidth = bufferedOne.getWidth();
        int totalHeight = bufferedTwo.getHeight();

        for(int x = 0; x < totalHeight; x++) {
            for(int y = 0; y < totalWidth; y++) {
                if(bufferedOne.getRGB(x,y) != bufferedTwo.getRGB(x,y)) {
                    return false;
                }
            }
        }

        return true;
    }

}
