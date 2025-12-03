package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class LocalFileSystemImageManager implements FileSystemImageManager {

    public List<BufferedImage> bufferedImages;

    public LocalFileSystemImageManager() {
        bufferedImages = new LinkedList<>();
    }

    @Override
    public BufferedImage loadImage(File imageFile) throws IOException {
        if (imageFile == null) {
            throw new IllegalArgumentException();
        }
        // if the file exists exception is thrown
        if (!imageFile.exists()) {
            throw new IOException("The file doesnt exists");
        }
        // if this is a directory or a symbolic link exception is thrown
        Path pathOfImage = Path.of(imageFile.getPath());
        if (imageFile.isDirectory() || Files.isSymbolicLink(pathOfImage)) {
            throw new IOException("This is not a regular file");
        }
        // only jpg, bmp and png files are supported
        String fileName = imageFile.getName().toLowerCase();
        if (!fileName.endsWith(".jpeg") && !fileName.endsWith(".jpg")
                && !fileName.endsWith(".png") && !fileName.endsWith(".bmp")) {
            throw new IOException("Unsupported file");
        }

        // loading the file
        try {
            BufferedImage loadedImage = ImageIO.read(imageFile);
            bufferedImages.add(loadedImage);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open file", e);
        }
        return bufferedImages.getLast();
    }

    @Override
    public List<BufferedImage> loadImagesFromDirectory(File imageDirectory) throws IOException {
        if (imageDirectory == null) {
            throw new IllegalArgumentException();
        }

        List<BufferedImage> imagesLoadFromDirectory = new LinkedList<>();

        // checks if the path exists
        Path pathToDirectory = imageDirectory.toPath();
        if (!Files.exists(pathToDirectory) || !imageDirectory.isDirectory()) {
            throw new IOException();
        }

        // all the files in the directory are going to be loaded to the program and added
        File[] files = imageDirectory.listFiles();
        for (File file : files) {
            imagesLoadFromDirectory.add(loadImage(file));
        }
        return imagesLoadFromDirectory;
    }

    @Override
    public void saveImage(BufferedImage image, File imageFile) throws IOException {
        if (image == null || imageFile == null) {
            throw new IllegalArgumentException();
        }

        // in case of invalid directory exception is thrown
        Path pathToFile = imageFile.toPath();
        if (pathToFile.getParent() != null && !Files.exists(pathToFile.getParent())) {
            throw new IOException("The parent dir doesnt exists");
        }

        // if the path exists
        if (Files.exists(imageFile.toPath())) {
            throw new IOException("The file already exists");
        }

        String nameOfFile = imageFile.getName();
        int indexOfDot = nameOfFile.lastIndexOf('.');

        String typeOfFile = nameOfFile.substring(indexOfDot + 1).toLowerCase();
        if (!typeOfFile.equals("jpg") && !typeOfFile.equals("jpeg") &&
                !typeOfFile.equals("png") && !typeOfFile.equals("bmp")) {
            throw new IOException("Unsupported file");
        }

        ImageIO.write(image, typeOfFile, imageFile);
    }
}