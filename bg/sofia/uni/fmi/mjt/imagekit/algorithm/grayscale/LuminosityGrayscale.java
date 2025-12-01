package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

import java.awt.image.BufferedImage;

public class LuminosityGrayscale implements GrayscaleAlgorithm{


    @Override
    public BufferedImage process(BufferedImage image) {

        int height = image.getHeight();
        int width = image.getWidth();

        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++){

                int currentPixel = image.getRGB(j,i);
                int transparent = (currentPixel >> 24) & 0xFF;
                int red = (currentPixel >> 16) & 0xFF;
                int green = (currentPixel >> 8) & 0xFF;
                int blue = currentPixel & 0xFF;

                int luminosity = (int) Math.round(((double) red * 0.21) + ((double) green * 0.72) + ((double)blue * 0.07));

                red = luminosity;
                green = luminosity;
                blue = luminosity;

                int newRGB = (transparent << 24) | (red << 16) | (green << 8) | blue;

                image.setRGB(j,i, newRGB);
            }
        }

        return image;
    }

}
