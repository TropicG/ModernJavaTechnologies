package bg.sofia.uni.fmi.mjt.music.server.repository.exception;

public class PlaylistAlreadyExistsException extends RuntimeException {
    public PlaylistAlreadyExistsException(String message) {
        super(message);
    }

    public PlaylistAlreadyExistsException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
