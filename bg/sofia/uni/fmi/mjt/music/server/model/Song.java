package bg.sofia.uni.fmi.mjt.music.server.model;

import java.util.Objects;

public record Song(String title, String artist, int duration) {

    @Override
    public boolean equals(Object object) {

        if (this == object) {
            return true;
        }

        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }

        Song otherSong = (Song) object;
        return title.equals(otherSong.title) && artist.equals(otherSong.artist);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, artist);
    }
}
