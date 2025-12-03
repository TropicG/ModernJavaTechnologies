package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.function.Try;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LocalFileSystemImageManagerTest {


    // test for:
    // public BufferedImage loadImage(File imageFile) throws IOException
    @Test
    void testNullImageForLoading() throws IOException {
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();
        assertThrows(IllegalArgumentException.class, () -> localFileSystemImageManager.loadImage(null),
                "When null is passed for loading an image, IllegalArgumentException shall be thrown");
    }

    @Test
    void testImageForLoadingDoestExists() {
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();
        File doesntExistsFile = new File("FileDoestExists.txt");
        assertThrows(IOException.class, () -> localFileSystemImageManager.loadImage(doesntExistsFile),
                "When a file that doesnt exists is passed for loading, IOException shall be thrown");
    }

    @Test
    void testDirectoryPassedForLoadingImage() throws IOException {
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();
        Path tempDir = Files.createTempDirectory("tempDir");

        try {
            assertThrows(IOException.class, () -> localFileSystemImageManager.loadImage(tempDir.toFile()),
                    "IOException shall be thrown when directory is given to loading image");
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    void testLoadingSymbolicLinkFileThrowsException() throws IOException {

        LocalFileSystemImageManager manager = new LocalFileSystemImageManager();

        Path targetFile = Files.createTempFile("realImage", ".png");
        Path linkPath = targetFile.getParent().resolve("myLink.png");

        Files.createSymbolicLink(linkPath, targetFile);

        try {
            assertThrows(IOException.class,
                    () -> manager.loadImage(linkPath.toFile()),
                    "Should throw IOException when loading from a symbolic link");
        } finally {
            Files.deleteIfExists(linkPath);
            Files.deleteIfExists(targetFile);
        }
    }


    @Test
    void testUnsupportedLoadingThrowsException() throws IOException {

        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();

        Path unsupportedPath = Path.of("schedule.txt");
        Files.createFile(unsupportedPath);

        try {
            assertThrows(IOException.class, () -> localFileSystemImageManager.loadImage(unsupportedPath.toFile()),
                    "When loading an unsupported type IOException shall be thrown");
        } finally {
            Files.delete(unsupportedPath);
        }
    }

    @Test
    void testLoadingPNG() throws IOException {
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();

        BufferedImage pngImage = new BufferedImage(10,10, BufferedImage.TYPE_INT_RGB);
        File pngFile = File.createTempFile("test-image", ".png");
        ImageIO.write(pngImage, "png", pngFile);

        try {
            assertTrue(compareBufferedImages(pngImage, localFileSystemImageManager.loadImage(pngFile)));
        } finally {
            Files.deleteIfExists(pngFile.toPath());
        }
    }

    @Test
    void testLoadingJPEG() throws IOException {
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();

        BufferedImage pngImage = new BufferedImage(10,10, BufferedImage.TYPE_INT_RGB);
        File pngFile = File.createTempFile("test-image", ".jpeg");
        ImageIO.write(pngImage, "jpeg", pngFile);

        try {
            assertTrue(compareBufferedImages(pngImage, localFileSystemImageManager.loadImage(pngFile)));
        } finally {
            Files.deleteIfExists(pngFile.toPath());
        }
    }

    @Test
    void testLoadingBMP() throws IOException {
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();

        BufferedImage pngImage = new BufferedImage(10,10, BufferedImage.TYPE_INT_RGB);
        File pngFile = File.createTempFile("test-image", ".bmp");
        ImageIO.write(pngImage, "bmp", pngFile);

        try {
            assertTrue(compareBufferedImages(pngImage, localFileSystemImageManager.loadImage(pngFile)));
        } finally {
            Files.deleteIfExists(pngFile.toPath());
        }
    }

    @Test
    void testLoadingJPG() throws IOException {
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();

        BufferedImage pngImage = new BufferedImage(10,10, BufferedImage.TYPE_INT_RGB);
        File pngFile = File.createTempFile("test-image", ".jpg");
        ImageIO.write(pngImage, "jpg", pngFile);

        try {
            assertTrue(compareBufferedImages(pngImage, localFileSystemImageManager.loadImage(pngFile)));
        } finally {
            Files.deleteIfExists(pngFile.toPath());
        }
    }

    // test for:
    // public void saveImage(BufferedImage image, File imageFile) throws IOException
    @Test
    void testSavingNullBufferedImageThrowsException() {
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();
        File saveTo = new File("file.dat");
        assertThrows(IllegalArgumentException.class, () -> localFileSystemImageManager.saveImage(null,saveTo),
                "When trying to save null value IllegalArgumentException is thrown");
    }

    @Test
    void testSavingNullFileLocThrowsException() throws IOException {
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();
        BufferedImage swampertPng = ImageIO.read(new File("swampert.png"));
        assertThrows(IllegalArgumentException.class, () -> localFileSystemImageManager.saveImage(swampertPng,null),
                "When trying to save null value IllegalArgumentException is thrown");
    }
    @Test
    void testSavingToInvalidDirThrowsException() throws IOException {
        Path invalidDir = Path.of("invalidDir" + File.separator + "invalidFile.txt");
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();
        BufferedImage swampertPng = ImageIO.read(new File("swampert.png"));
        assertThrows(IOException.class, () -> localFileSystemImageManager.saveImage(swampertPng,invalidDir.toFile()),
                "When given invalid directory IOException shall be thrown ");
    }

    @Test
    void testSaveImageThrowsExceptionIfParentDirectoryDoesntExists() throws IOException {
        File parentDirecotryDoestExists = new File("dir_doest_exists/image.png");
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();
        BufferedImage swampertPng = ImageIO.read(new File("swampert.png"));
        assertThrows(IOException.class, () -> localFileSystemImageManager.saveImage(swampertPng, parentDirecotryDoestExists),
                "Should throw IOException if the parent directory does not exist");
    }

    @Test
    void testSaveImageThrowsExceptionIfParentDirectoryNull() throws IOException {
        File parentDirecotryDoestExists = new File("dir_doest_exists/image.png");
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();
        BufferedImage swampertPng = ImageIO.read(new File("swampert.png"));
        assertThrows(IOException.class, () -> localFileSystemImageManager.saveImage(swampertPng, parentDirecotryDoestExists),
                "Should throw IOException if the parent directory does not exist");
    }

    @Test
    void testSaveImageThrowsExceptionIfFileExists() throws IOException {
        File parentDirecotryDoestExists = new File("swampert.png");
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();
        BufferedImage swampertPng = ImageIO.read(new File("swampert.png"));
        assertThrows(IOException.class, () -> localFileSystemImageManager.saveImage(swampertPng, parentDirecotryDoestExists),
                "Should throw IOException if the parent directory does not exist");
    }

    @Test
    void testSaveImageThrowsExceptionIfFileAlreadyExists() throws IOException {
        Path existingPath = Path.of("swampert.png");
        BufferedImage swampertPng = ImageIO.read(new File("swampert.png"));
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();
        assertThrows(IOException.class, () -> localFileSystemImageManager.saveImage(swampertPng, existingPath.toFile()),
                    "Should throw IOException if the file already exists");
    }

    @Test
    void testSaveImageThrowsExceptionForUnsupportedFileExtension() throws IOException {
        File unsupportedFile = new File("image.txt");
        BufferedImage swampertPng = ImageIO.read(new File("swampert.png"));
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();
        assertThrows(IOException.class,
                () -> localFileSystemImageManager.saveImage(swampertPng, unsupportedFile),
                "Should throw IOException for unsupported file extensions like .txt");
    }

    @Test
    void testValidFilePNGSaved() throws IOException {

        BufferedImage imageForSaving = new BufferedImage(1980,1080, BufferedImage.TYPE_INT_RGB);
        File toSave = new File("newImage.png");

        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();

        try {
            assertDoesNotThrow(() -> localFileSystemImageManager.saveImage(imageForSaving, toSave));
        }
        finally {
            Files.deleteIfExists(toSave.toPath());
        }

    }

    @Test
    void testValidFileJPEGSaved() throws IOException {

        BufferedImage imageForSaving = new BufferedImage(1980,1080, BufferedImage.TYPE_INT_RGB);
        File toSave = new File("newImage.jpeg");

        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();

        try {
            assertDoesNotThrow(() -> localFileSystemImageManager.saveImage(imageForSaving, toSave));
        }
        finally {
            Files.deleteIfExists(toSave.toPath());
        }

    }

    @Test
    void testValidFileBMPSaved() throws IOException {

        BufferedImage imageForSaving = new BufferedImage(1980,1080, BufferedImage.TYPE_INT_RGB);
        File toSave = new File("newImage.bmp");

        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();

        try {
            assertDoesNotThrow(() -> localFileSystemImageManager.saveImage(imageForSaving, toSave));
        }
        finally {
            Files.deleteIfExists(toSave.toPath());
        }

    }

    @Test
    void testValidFileJPGSaved() throws IOException {

        BufferedImage imageForSaving = new BufferedImage(1980,1080, BufferedImage.TYPE_INT_RGB);
        File toSave = new File("newImage.jpg");

        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();

        try {
            assertDoesNotThrow(() -> localFileSystemImageManager.saveImage(imageForSaving, toSave));
        }
        finally {
            Files.deleteIfExists(toSave.toPath());
        }

    }

    // test for:
    // public List<BufferedImage> loadImagesFromDirectory(File imageDirectory) throws IOException

    @Test
    void testLoadImageFromDirWhenDirIsNull() {
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();
        assertThrows(IllegalArgumentException.class, () -> localFileSystemImageManager.loadImagesFromDirectory(null),
                "Directory cannot be null, when loading images from dir");
    }

    @Test
    void testLoadImageFromDirWhenGivenDirIsOnlyFile() throws IOException {
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();
        Path dirToFile = Path.of("file.png");

        Files.deleteIfExists(dirToFile);
        Files.createFile(dirToFile);

        try {
            assertThrows(IOException.class,
                    () -> localFileSystemImageManager.loadImagesFromDirectory(dirToFile.toFile()),
                    "Should throw IOException when a regular file is passed instead of a directory");
        } finally {
            Files.deleteIfExists(dirToFile);
        }
    }

    @Test
    void testLoadImageFromDirWhenGivenDirDoesntExists() throws IOException {
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();
        Path dirToFile = Path.of("doesntExistsDir/");

        assertThrows(IOException.class, () -> localFileSystemImageManager.loadImagesFromDirectory(dirToFile.toFile()),
                "Should throw IOException when a dir is passed that doesnt exists");
    }

    @Test
    void testLoadImageFromDirSuccessfully() throws IOException {
        LocalFileSystemImageManager localFileSystemImageManager = new LocalFileSystemImageManager();
        Path temporaryDir = Files.createTempDirectory("tempDirectory");

        File image1 = temporaryDir.resolve("test1.png").toFile();
        File image2 = temporaryDir.resolve("test2.png").toFile();

        BufferedImage dummyImg = new BufferedImage(10,10, BufferedImage.TYPE_INT_RGB);

        ImageIO.write(dummyImg, "png", image1);
        ImageIO.write(dummyImg, "jpg", image2);

        try {
            List<BufferedImage> result = localFileSystemImageManager.loadImagesFromDirectory(temporaryDir.toFile());
            assertEquals(2, result.size(), "When two files are in a directory, two files should be returned from there");
        } finally {
            Files.deleteIfExists(image1.toPath());
            Files.deleteIfExists(image2.toPath());
            Files.deleteIfExists(temporaryDir);
        }
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
