package bg.sofia.uni.fmi.mjt.music.server.repository;

import bg.sofia.uni.fmi.mjt.music.server.model.Playlist;
import bg.sofia.uni.fmi.mjt.music.server.model.Song;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.PlaylistAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.PlaylistNotFoundException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.SongAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.SongNotFoundException;

import java.util.Collection;

public class InMemoryPlaylistRepository implements  PlaylistRepository {

    @Override
    public void createPlaylist(String playlistName) throws PlaylistAlreadyExistsException {
        // TO DO IMPLEMENTATION
    }

    @Override
    public Song addSong(String playlistName, String songTitle, String artistName, int duration)
            throws PlaylistNotFoundException, SongAlreadyExistsException {
        // TO DO IMPLEMENTATION
        return null;
    }

    @Override
    public int likeSong(String playlistName, String songTitle, String artistName)
            throws PlaylistNotFoundException, SongNotFoundException {
        // TO DO IMPLEMENTATION
        return 0;
    }

    @Override
    public int unlikeSong(String playlistName, String songTitle, String artistName)
            throws PlaylistNotFoundException, SongNotFoundException {
        // TO DO IMPLEMENTATION
        return 0;
    }

    @Override
    public Collection<String> getAllPlaylists() {
        // TO DO IMPLEMENTATION
        return null;
    }

    @Override
    public Playlist getPlaylist(String playlistName) throws PlaylistNotFoundException {
        // TO DO IMPLEMENTATION
        return null;
    }

}
