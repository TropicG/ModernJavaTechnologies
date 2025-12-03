package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.LuminosityGrayscale;
import bg.sofia.uni.fmi.mjt.imagekit.filesystem.LocalFileSystemImageManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SobelEdgeDetectionTest {

    @Mock
    private ImageAlgorithm imageAlgorithm;

    @InjectMocks
    private SobelEdgeDetection sobelEdgeDetection;

    @Test
    void testNullInputSobelAlg() {
        assertThrows(IllegalArgumentException.class, () -> sobelEdgeDetection.process(null),
                "Null cannot be passed to sobel algorithm");
    }

    @Test
    void testSuccessfulEdgeDetection() throws IOException {

        LuminosityGrayscale luminosityGrayscale = new LuminosityGrayscale();
        SobelEdgeDetection sobelEdgeDetection1 = new SobelEdgeDetection(luminosityGrayscale);
        Random random = new Random();

        BufferedImage bufferedImage = new BufferedImage(100,100, BufferedImage.TYPE_INT_RGB);
        for(int x = 0; x < 10; x++){
            for(int y = 0; y < 10; y++){
                bufferedImage.setRGB(x,y, random.nextInt(Integer.MIN_VALUE, Integer.MAX_VALUE));
            }
        }
        BufferedImage bufferedImageAfterGrayscale = luminosityGrayscale.process(bufferedImage);
        when(imageAlgorithm.process(bufferedImage)).thenReturn(bufferedImageAfterGrayscale);

        BufferedImage bufferedImageAfterEdgeDetection = this.sobelEdgeDetection.process(bufferedImage);

        assertTrue(compareBufferedImages(bufferedImageAfterEdgeDetection, this.sobelEdgeDetection.process(bufferedImage)));
    }

    /*
    @Test
    void testSuccessfulEdgeDetection() throws IOException {

        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();

        File expectedEdgeDetection = new File("swampert-edge-detected.png");
        File expectedGrayScaled = new File("swampert-grayscale.png");
        File inputImage = new File("swampert.png");

        BufferedImage inputImageBuffer = localFileSystemImageManager.loadImage(inputImage);
        BufferedImage expectedEdgeDetectionBuffer = localFileSystemImageManager.loadImage(expectedEdgeDetection);
        BufferedImage expectedGrayscaledBuffer = localFileSystemImageManager.loadImage(expectedGrayScaled);

        when(imageAlgorithm.process(inputImageBuffer)).thenReturn(expectedGrayscaledBuffer);
        BufferedImage afterEdgeDetection = sobelEdgeDetection.process(inputImageBuffer);
        assertTrue(compareBufferedImages(afterEdgeDetection, expectedEdgeDetectionBuffer),
                "After algorithm there not right");
    }
    */


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
