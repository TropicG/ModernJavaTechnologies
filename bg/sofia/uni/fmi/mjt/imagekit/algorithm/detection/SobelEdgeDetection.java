package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.GrayscaleAlgorithm;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.LuminosityGrayscale;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SobelEdgeDetection implements EdgeDetectionAlgorithm {

    private static List<SobelCoordinate> Gx;
    private static List<SobelCoordinate> Gy;

    ImageAlgorithm grayscaleAlgorithm;

    static {
        Gx = new ArrayList<>();
        // Лява колона (x = -1)
        Gx.add(new SobelCoordinate(-1, -1, -1));
        Gx.add(new SobelCoordinate(-1,  0, -2));
        Gx.add(new SobelCoordinate(-1,  1, -1));

        // Средна колона (x = 0) -> Всичко е 0
        Gx.add(new SobelCoordinate( 0, -1,  0));
        Gx.add(new SobelCoordinate( 0,  0,  0));
        Gx.add(new SobelCoordinate( 0,  1,  0));

        // Дясна колона (x = 1)
        Gx.add(new SobelCoordinate( 1, -1,  1));
        Gx.add(new SobelCoordinate( 1,  0,  2));
        Gx.add(new SobelCoordinate( 1,  1,  1));

        Gy = new ArrayList<>();
        //first row
        // Горен ред (y = -1)
        Gy.add(new SobelCoordinate(-1, -1, -1));
        Gy.add(new SobelCoordinate( 0, -1, -2));
        Gy.add(new SobelCoordinate( 1, -1, -1));

        // Среден ред (y = 0) -> Всичко е 0
        Gy.add(new SobelCoordinate(-1,  0,  0));
        Gy.add(new SobelCoordinate( 0,  0,  0));
        Gy.add(new SobelCoordinate( 1,  0,  0));

        // Долен ред (y = 1)
        Gy.add(new SobelCoordinate(-1,  1,  1));
        Gy.add(new SobelCoordinate( 0,  1,  2));
        Gy.add(new SobelCoordinate( 1,  1,  1));
    }

    private boolean isValidLocation(int x, int y, SobelCoordinate sobelCoordinate, int picWidth, int picHeight) {

        if((x + sobelCoordinate.x() > (picWidth - 1)) || (x + sobelCoordinate.x() < 0)) {
            return false;
        }
        else if((y + sobelCoordinate.y() > (picHeight - 1)) || (y + sobelCoordinate.y() < 0)) {
            return false;
        }

        return true;
    }

    public SobelEdgeDetection(ImageAlgorithm grayscaleAlgorithm) {
        this.grayscaleAlgorithm = grayscaleAlgorithm;
    }

    @Override
    public BufferedImage process(BufferedImage image) {

        if(image == null) {
            throw new IllegalArgumentException();
        }

        BufferedImage grayScaledImage = grayscaleAlgorithm.process(image);
        BufferedImage sobelEdgeImage = new BufferedImage(grayScaledImage.getWidth(), grayScaledImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        int width = sobelEdgeImage.getWidth();
        int height = sobelEdgeImage.getHeight();

        for(int w = 1; w < width - 1; w++){
            for(int h = 1; h < height - 1; h++){

                int totalGx = 0;
                int totalGy = 0;
                // calculating Gx

                for(int coordinates = 0; coordinates < Gx.size(); coordinates++){
                    // calc Gx
                    if(isValidLocation(w,h, Gx.get(coordinates), width, height)) {
                        int pixelAtPosition = grayScaledImage.getRGB(w + Gx.get(coordinates).x(), h + Gx.get(coordinates).y());
                        int color = (pixelAtPosition >> 16) & 0xFF;
                        totalGx += (color * Gx.get(coordinates).coefficient());
                    }
                    else {
                        totalGx += 0;
                    }

                    // calc Gy
                    if(isValidLocation(w,h, Gy.get(coordinates), width, height)) {
                        int pixelAtPosition = grayScaledImage.getRGB( w + Gy.get(coordinates).x(), h + Gy.get(coordinates).y());
                        int color = (pixelAtPosition >> 16) & 0xFF;
                        totalGy += (color * Gy.get(coordinates).coefficient());
                    }
                    else {
                        totalGy += 0;
                    }
                }

                double totalG = Math.sqrt(Math.pow(totalGx, 2) + Math.pow(totalGy, 2));
                int newPixelValue = Math.min(255, (int)Math.round(totalG));
                int newRg = (newPixelValue << 16) | (newPixelValue << 8) | newPixelValue;
                sobelEdgeImage.setRGB(w,h, newRg);
            }
        }

        return sobelEdgeImage;
    }

    public static record SobelCoordinate(int x, int y, int coefficient) {
    }
}
