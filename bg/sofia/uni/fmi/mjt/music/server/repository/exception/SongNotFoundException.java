package bg.sofia.uni.fmi.mjt.music.server.repository.exception;

public class SongNotFoundException extends RuntimeException {
    public SongNotFoundException(String message) {
        super(message);
    }

    public SongNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
