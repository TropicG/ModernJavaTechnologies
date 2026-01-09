package bg.sofia.uni.fmi.mjt.music.server.repository.exception;

public class SongAlreadyExistsException extends RuntimeException {
    public SongAlreadyExistsException(String message) {
        super(message);
    }

    public SongAlreadyExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

