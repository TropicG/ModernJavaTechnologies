package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import java.awt.image.BufferedImage;

public class LuminosityGrayscale implements GrayscaleAlgorithm {

    private static final int ONE_BYTE;
    private static final int TWO_BYTES;

    private static final int MAX_BYTE;

    private static final double RED_COEF;
    private static final double GREEN_COEF;
    private static final double BLUE_COEF;

    static {
        ONE_BYTE = 8;
        TWO_BYTES = 16;

        MAX_BYTE = 0xFF;

        RED_COEF = 0.21d;
        GREEN_COEF = 0.72d;
        BLUE_COEF = 0.07d;
    }


    private int calculateLuminosity(int currentPixel) {

        // getting the Red, Green, Blue color
        int red = (currentPixel >> TWO_BYTES) & MAX_BYTE;
        int green = (currentPixel >> ONE_BYTE) & MAX_BYTE;
        int blue = currentPixel & MAX_BYTE;

        // calculating the liminosity based on this formula: 0.21 R + 0.72 G + 0.07 B
        return (int) Math.round(((double) red * RED_COEF) +
                ((double) green * GREEN_COEF) + ((double) blue * BLUE_COEF));
    }

    @Override
    public BufferedImage process(BufferedImage image) {

        if(image == null) {
            throw new IllegalArgumentException();
        }

        int width = image.getWidth();
        int height = image.getHeight();

        // the new grey pic will appear here
        BufferedImage greyPic = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);

        // the pic is based on 2d array with widht x height size, each pixel is element in this array
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                // transparency and new values for R, G, B
                int luminosity = calculateLuminosity(image.getRGB(x, y));

                // creating the new pixel
                int newRGB = (luminosity << TWO_BYTES)
                        | (luminosity << ONE_BYTE) | luminosity;

                greyPic.setRGB(x, y, newRGB);
            }
        }

        return greyPic;
    }
}
