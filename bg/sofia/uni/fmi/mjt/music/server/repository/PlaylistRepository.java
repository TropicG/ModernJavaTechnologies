package bg.sofia.uni.fmi.mjt.music.server.repository;

import bg.sofia.uni.fmi.mjt.music.server.model.Playlist;
import bg.sofia.uni.fmi.mjt.music.server.model.Song;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.PlaylistAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.PlaylistNotFoundException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.SongAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.SongNotFoundException;

import java.util.Collection;

public interface PlaylistRepository {

    /**
     * Creates a new playlist with the given name.
     *
     * @param playlistName the name of the playlist to be created.
     *                     The name must be a single word (no whitespaces).
     * @throws PlaylistAlreadyExistsException if a playlist with the given name
     *                                        already exists.
     */
    void createPlaylist(String playlistName) throws PlaylistAlreadyExistsException;

    /**
     * Adds a new song to an existing playlist.
     *
     * @param playlistName the name of the playlist to which the song will be added.
     * @param songTitle    the title of the song.
     * @param artistName   the name of the artist.
     * @param duration     the duration of the song in seconds.
     * @return the Song added.
     * @throws PlaylistNotFoundException  if the playlist with the given name
     *                                    does not exist.
     * @throws SongAlreadyExistsException if a song with the same title and artist
     *                                    already exists in the playlist.
     */
    Song addSong(String playlistName, String songTitle, String artistName, int duration)
            throws PlaylistNotFoundException, SongAlreadyExistsException;

    /**
     * Increases the number of likes of a given song in the playlist by 1.
     *
     * @param playlistName the name of the playlist.
     * @param songTitle    the title of the song.
     * @param artistName   the name of the artist.
     * @return the updated number of likes for the song.
     * @throws PlaylistNotFoundException if the playlist does not exist.
     * @throws SongNotFoundException     if the song does not exist in the playlist.
     */
    int likeSong(String playlistName, String songTitle, String artistName)
            throws PlaylistNotFoundException, SongNotFoundException;

    /**
     * Decreases the number of likes of a given song in the playlist by 1.
     * The number of likes cannot be less than 0. When unliking a song with 0 likes,
     * the operation succeeds silently with likes remaining 0.
     *
     * @param playlistName the name of the playlist.
     * @param songTitle    the title of the song.
     * @param artistName   the name of the artist.
     * @return the updated number of likes for the song.
     * @throws PlaylistNotFoundException if the playlist does not exist.
     * @throws SongNotFoundException     if the song does not exist in the playlist.
     */
    int unlikeSong(String playlistName, String songTitle, String artistName)
            throws PlaylistNotFoundException, SongNotFoundException;

    /**
     * Retrieves the names of all existing playlists.
     *
     * @return a collection containing the names of all playlists.
     * If no playlists exist, an empty collection is returned.
     */
    Collection<String> getAllPlaylists();

    /**
     * Retrieves detailed information about a playlist with the given name.
     *
     * @param playlistName the name of the playlist to retrieve.
     * @return the {@link Playlist} object containing information about the playlist
     * and all of its songs.
     * @throws PlaylistNotFoundException if the playlist does not exist.
     */
    Playlist getPlaylist(String playlistName) throws PlaylistNotFoundException;

}