package bg.sofia.uni.fmi.mjt.imagekit;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection.SobelEdgeDetection;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.LuminosityGrayscale;
import bg.sofia.uni.fmi.mjt.imagekit.filesystem.FileSystemImageManager;
import bg.sofia.uni.fmi.mjt.imagekit.filesystem.LocalFileSystemImageManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        /*
        //867 x 911
        LuminosityGrayscale luminosityGrayscale = new LuminosityGrayscale();
        SobelEdgeDetection sobelEdgeDetection = new SobelEdgeDetection();
        File mudkip = new File("kitten.png");


        BufferedImage coloredImage = ImageIO.read(mudkip);
        BufferedImage newColorImage = sobelEdgeDetection.process(coloredImage);

        ImageIO.write(newColorImage, "png", new File("kitten_new.png"));
        */


        FileSystemImageManager fsImageManager = new LocalFileSystemImageManager();

        BufferedImage image = fsImageManager.loadImage(new File("kitten.png"));

        ImageAlgorithm grayscaleAlgorithm = new LuminosityGrayscale();
        BufferedImage grayscaleImage = grayscaleAlgorithm.process(image);

        ImageAlgorithm sobelEdgeDetection = new SobelEdgeDetection(grayscaleAlgorithm);
        BufferedImage edgeDetectedImage = sobelEdgeDetection.process(image);

        fsImageManager.saveImage(grayscaleImage, new File("kitten-grayscale.png"));
        fsImageManager.saveImage(edgeDetectedImage, new File("kitten-edge-detected.png"));
    }

}
