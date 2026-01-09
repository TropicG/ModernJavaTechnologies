package bg.sofia.uni.fmi.mjt.music.server.repository.exception;

public class PlaylistNotFoundException extends RuntimeException {
    public PlaylistNotFoundException(String message) {
        super(message);
    }

    public PlaylistNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
