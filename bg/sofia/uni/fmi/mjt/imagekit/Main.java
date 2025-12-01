package bg.sofia.uni.fmi.mjt.imagekit;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.LuminosityGrayscale;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        //867 x 911
        LuminosityGrayscale luminosityGrayscale = new LuminosityGrayscale();
        File mudkip = new File("mudkip.jpg");


        BufferedImage coloredImage = ImageIO.read(mudkip);
        coloredImage = luminosityGrayscale.process(coloredImage);

        ImageIO.write(coloredImage, "jpg", new File("mudkip_new.jpg"));
    }

}
