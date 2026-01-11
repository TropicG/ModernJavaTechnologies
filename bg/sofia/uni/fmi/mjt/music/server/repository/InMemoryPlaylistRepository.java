package bg.sofia.uni.fmi.mjt.music.server.repository;

import bg.sofia.uni.fmi.mjt.music.server.model.Playlist;
import bg.sofia.uni.fmi.mjt.music.server.model.Song;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.PlaylistAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.PlaylistNotFoundException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.SongAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.SongNotFoundException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InMemoryPlaylistRepository implements PlaylistRepository {

    private final Map<String, Playlist> playlists;

    public InMemoryPlaylistRepository() {
        this.playlists = new HashMap<>();
    }

    @Override
    public void createPlaylist(String playlistName) throws PlaylistAlreadyExistsException {
        // if the playlist exists throw exception
        if (playlists.containsKey(playlistName)) {
            throw new PlaylistAlreadyExistsException("The provided name for the playlist already exists in the database");
        }

        // creating the new playlist and adding it to the repo
        Playlist newPlaylist = new Playlist(playlistName);
        playlists.put(playlistName, newPlaylist);
    }

    @Override
    public Song addSong(String playlistName, String songTitle, String artistName, int duration)
            throws PlaylistNotFoundException, SongAlreadyExistsException {
        // if the playlist doesn't exist in the repo it will throw exception
        if (!playlists.containsKey(playlistName)) {
            throw new PlaylistNotFoundException("This playlist doesnt exists");
        }

        // if the duration of the song is below zero we throw an exception
        if (duration <= 0) {
            throw new IllegalAccessError("The song cannot be with 0 or below duration");
        }

        // here SongAlreadyExistsException will be thrown if the song was added in the function
        Song newSong = new Song(songTitle, artistName, duration);
        playlists.get(playlistName).addSong(newSong);

        return newSong;
    }

    @Override
    public int likeSong(String playlistName, String songTitle, String artistName)
            throws PlaylistNotFoundException, SongNotFoundException {
        // if the playlist doesn't exist in the repo it will throw exception
        if (!playlists.containsKey(playlistName)) {
            throw new PlaylistNotFoundException("This playlist doesnt exists");
        }

        // increments the like counts of the song in the playlists, it can throw SongNotFoundException
        Song songLikesToBeIncremented = new Song(songTitle, artistName, 0);
        playlists.get(playlistName).likeSong(songLikesToBeIncremented);

        // return the like count of the song in the playlist
        return playlists.get(playlistName).getLikesForSong(songLikesToBeIncremented);
    }

    @Override
    public int unlikeSong(String playlistName, String songTitle, String artistName)
            throws PlaylistNotFoundException, SongNotFoundException {
        // if the playlist doesn't exist in the repo it will throw exception
        if (!playlists.containsKey(playlistName)) {
            throw new PlaylistNotFoundException("This playlist doesnt exists");
        }

        // decrement the likes count of a song in the playlist, in case the song is missing
        // SongNotFoundException shall be thrown
        Song songLikesToBeDecremented = new Song(songTitle, artistName, 0);
        playlists.get(playlistName).dislikeSong(songLikesToBeDecremented);

        // return the like count of the song in the playlist
        return playlists.get(playlistName).getLikesForSong(songLikesToBeDecremented);
    }

    @Override
    public Collection<String> getAllPlaylists() {
        return Collections.unmodifiableCollection(playlists.keySet());
    }

    @Override
    public Playlist getPlaylist(String playlistName) throws PlaylistNotFoundException {
        // if the playlist name cannot be found in the repo, exception will be thrown
        if (!playlists.containsKey(playlistName)) {
            throw new PlaylistNotFoundException("The specific playlist cannot be found");
        }

        return playlists.get(playlistName);
    }
}
