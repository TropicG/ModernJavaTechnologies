package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class SobelEdgeDetection implements EdgeDetectionAlgorithm {

    private static final List<SobelCoordinate> GX;
    private static final List<SobelCoordinate> GY;


    private static final int NEG_TWO = -2;
    private static final int TWO = 2;

    private static final int TWO_BYTES = 16;
    private static final int ONE_BYTE = 8;

    private static final int MAX_8BITS = 255;

    // representing on which coordinates the pixels are for Sobe algorithm, as well as their coefficient
    private record SobelCoordinate(int x, int y, int coefficient) {
    }

    static {
        GX = new ArrayList<>();
        // left column
        GX.add(new SobelCoordinate(-1, -1, -1));
        GX.add(new SobelCoordinate(-1, 0, NEG_TWO));
        GX.add(new SobelCoordinate(-1, 1, -1));
        // central column
        GX.add(new SobelCoordinate(0, -1, 0));
        GX.add(new SobelCoordinate(0, 0, 0));
        GX.add(new SobelCoordinate(0, 1, 0));
        // right column
        GX.add(new SobelCoordinate(1, -1, 1));
        GX.add(new SobelCoordinate(1, 0, TWO));
        GX.add(new SobelCoordinate(1, 1, 1));
    }

    static {
        GY = new ArrayList<>();
        // left row
        GY.add(new SobelCoordinate(-1, -1, -1));
        GY.add(new SobelCoordinate(0, -1, NEG_TWO));
        GY.add(new SobelCoordinate(1, -1, -1));
        // central row
        GY.add(new SobelCoordinate(-1, 0, 0));
        GY.add(new SobelCoordinate(0, 0, 0));
        GY.add(new SobelCoordinate(1, 0, 0));
        // right row
        GY.add(new SobelCoordinate(-1, 1, 1));
        GY.add(new SobelCoordinate(0, 1, TWO));
        GY.add(new SobelCoordinate(1, 1, 1));
    }

    ImageAlgorithm grayscaleAlgorithm;

    public SobelEdgeDetection(ImageAlgorithm grayscaleAlgorithm) {
        this.grayscaleAlgorithm = grayscaleAlgorithm;
    }

    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException();
        }

        BufferedImage grayScaledImage = grayscaleAlgorithm.process(image);
        BufferedImage sobelEdgeImage = new BufferedImage(grayScaledImage.getWidth(), grayScaledImage.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        int width = sobelEdgeImage.getWidth();
        int height = sobelEdgeImage.getHeight();

        // for every pixel, calculating Gx and Gy (coeficient with neighrbos), total G and generating the new PixelValue
        // after that the new generated rgb is going to be set for Red Green and Blue for the pixel
        for (int w = 1; w < width - 1; w++) {
            for (int h = 1; h < height - 1; h++) {
                int totalGx = calculateGx(w, h, width, height, grayScaledImage);
                int totalGy = calculateGy(w, h, width, height, grayScaledImage);
                double totalG = Math.sqrt((totalGx * totalGx) + (totalGy * totalGy));
                int newPixelValue = Math.min(MAX_8BITS, (int) Math.round(totalG));
                int newRGB = (newPixelValue << TWO_BYTES) | (newPixelValue << ONE_BYTE) | newPixelValue;
                sobelEdgeImage.setRGB(w, h, newRGB);
            }
        }
        return sobelEdgeImage;
    }

    private int calculateGx(int w, int h, int width, int height, BufferedImage grayScaledImage) {
        // calc Gx for every around neighbor for the given pixel
        int totalGx = 0;
        for (SobelCoordinate gx : GX) {
            int pixelAtPosition = grayScaledImage.getRGB(w + gx.x(), h + gx.y());
            int color = (pixelAtPosition >> TWO_BYTES) & MAX_8BITS;
            totalGx += (color * gx.coefficient());
        }

        return totalGx;
    }

    private int calculateGy(int w, int h, int width, int height, BufferedImage grayScaledImage) {
        // calc Gy for every around neighbor for the given pixel
        int totalGy = 0;
        for (SobelCoordinate gy : GY) {
            int pixelAtPosition = grayScaledImage.getRGB(w + gy.x(), h + gy.y());
            int color = (pixelAtPosition >> TWO_BYTES) & MAX_8BITS;
            totalGy += (color * gy.coefficient());
        }

        return totalGy;
    }
}
