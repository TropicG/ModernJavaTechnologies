package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import bg.sofia.uni.fmi.mjt.imagekit.filesystem.LocalFileSystemImageManager;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

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
    void testSuccessfullyGrayscale() {
        LuminosityGrayscale luminosityGrayscale = new LuminosityGrayscale();
        Random random = new Random();

        BufferedImage bufferedImage = new BufferedImage(100,100, BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < 10; x++){
            for(int y = 0; y < 10; y++){
                bufferedImage.setRGB(x,y, random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
        }
        BufferedImage bufferedImageAfterGrayscale = luminosityGrayscale.process(bufferedImage);

        assertTrue(compareBufferedImages(bufferedImageAfterGrayscale, luminosityGrayscale.process(bufferedImage)),
                "There is a problem with grayscale");
    }

    private boolean compareBufferedImages(BufferedImage bufferedOne, BufferedImage bufferedTwo) {
        if(!(bufferedOne.getWidth() == bufferedTwo.getWidth() && bufferedOne.getHeight() == bufferedTwo.getHeight())){
            return false;
        }

        int totalWidth = bufferedOne.getWidth();
        int totalHeight = bufferedTwo.getHeight();

        for(int x = 0; x < totalWidth; x++) {
            for(int y = 0; y < totalHeight; y++) {
                if(bufferedOne.getRGB(x,y) != bufferedTwo.getRGB(x,y)) {
                    return false;
                }
            }
        }

        return true;
    }

}
