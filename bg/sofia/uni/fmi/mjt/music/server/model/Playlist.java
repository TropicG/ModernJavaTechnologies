package bg.sofia.uni.fmi.mjt.music.server.model;

import bg.sofia.uni.fmi.mjt.music.server.repository.exception.SongAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.SongNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class Playlist {

    private final Map<Song, Integer> songs;
    private final String name;

    public Playlist(String name) {
        this.name = name;
        this.songs = new HashMap<>();
    }

    public Playlist(String name, Map<Song, Integer> songs) {
        this.name = name;
        this.songs = songs;
    }

    public void addSong(Song newSong) {
        // if the song already exists in the playlist throw exception
        if (songs.containsKey(newSong)) {
            throw new SongAlreadyExistsException("The song is already added to the playlist");
        }

        // adding the song with zero likes
        songs.put(newSong, 0);
    }

    public void likeSong(Song song) {
        // if the songs doesn't exist in this playlist it will throw exception
        if (!songs.containsKey(song)) {
            throw new SongNotFoundException("The song cannot be found in this playlist");
        }

        int newValue = songs.get(song) + 1;
        songs.put(song, newValue);
    }

    public int getLikesForSong(Song song) {
        // if the songs doesn't exist in this playlist it will throw exception
        if (!songs.containsKey(song)) {
            throw new SongNotFoundException("The song cannot be found in this playlist");
        }

        return songs.get(song);
    }

    public void dislikeSong(Song song) {
        if (!songs.containsKey(song)) {
            throw new SongNotFoundException("The song cannot be found in this playlist");
        }

        int newValue = songs.get(song) - 1;
        songs.put(song, newValue);
    }

    public Map<Song, Integer> getSongs() {
        return songs;
    }

    public String getPlaylistName() {
        return name;
    }

}
