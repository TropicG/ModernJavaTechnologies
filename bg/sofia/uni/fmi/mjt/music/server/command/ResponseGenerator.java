package bg.sofia.uni.fmi.mjt.music.server.command;

import bg.sofia.uni.fmi.mjt.music.server.model.Playlist;
import bg.sofia.uni.fmi.mjt.music.server.model.Song;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

class ResponseGenerator {
    private static final String RESPONSE_OK = "{\"status\":\"OK\",\"message\":";
    private static final String RESPONSE_OK_MULTI_PLAYLIST = "{\"status\":\"OK\",\"playlists\":[";
    private static final String RESPONSE_OK_SINGLE_PLAYLIST = "{\"status\":\"OK\",\"playlist\":{\"name\":\"";
    private static final String RESPONSE_ERROR = "{\"status\":\"ERROR\",\"message\":";
    private static final String UNKNOWN_COMMAND = "{\"status\":\"ERROR\",\"message\":\"Unknown command\"}";
    private static final String NOT_ENOUGH_ARGUMENTS =
            "{\"status\":\"ERROR\",\"message\":\"Expected ";

    public static final String RESPONSE_ERROR_EXPECTED_COMMAND = "{\"status\":\"ERROR\",\"message\":\"Unknown command\"}";

    // void createPlaylist(String playlistName) throws PlaylistAlreadyExistsException
    public static String generateOKPlaylistCreation(String playlistName) {
        return RESPONSE_OK + "\"Playlist " + playlistName + " created successfully.\"}";
    }

    public static String generateERRORPlaylistCreation(String playlistName) {
        return RESPONSE_ERROR + "\"Playlist " + playlistName + " already exists.\"}";
    }

    //  Song addSong(String playlistName, String songTitle, String artistName, int duration)
    //            throws PlaylistNotFoundException, SongAlreadyExistsException;
    public static String generateOKSongAddedToPlaylist(String songTitle, String artist) {
        return RESPONSE_OK + "\"Song " + songTitle + " by " + artist + " added successfully.\"}";
    }

    public static String generateErrorSongWithZeroDuration() {
        return RESPONSE_ERROR + "\"Duration must be a positive integer\"}";
    }

    public static String generateMissingPlaylist(String playlistName) {
        return RESPONSE_ERROR + "\"Playlist " + playlistName + "does not exist.\"}";
    }

    public static String generateErrorSongAlreadyExistsInPlaylist
            (String playlistName, String songTitle, String artist) {
        return RESPONSE_ERROR + "\"Song " + songTitle + " by " + artist + " already exists in playlist " +
                playlistName + ".\"}";
    }

    public static String generateErrorInvalidDurationForSong(String duration) {
        return RESPONSE_ERROR + "\"Invalid argument for duration: " + duration + ".\"}";
    }

    // int likeSong(String playlistName, String songTitle, String artistName)
    //            throws PlaylistNotFoundException, SongNotFoundException;
    public static String generateOKSongLikeCountUpdated(String songTitle, String artistName, int duration) {
        return RESPONSE_OK + "\"Song " + songTitle + " by " + artistName + " liked. Likes: " + duration + "\"}";
    }

    public static String generateErrorSongNotFound(String songTitle, String artistName, String playlistName) {
        return RESPONSE_ERROR
                + "\"Song " + songTitle + " by " + artistName + " does not exist in playlist " + playlistName + ".\"}";
    }

    public static String generateOKAllPlaylist(Collection<String> playlistNames) {
        String responseToClient = RESPONSE_OK_MULTI_PLAYLIST;

        Iterator<String> iter = playlistNames.iterator();
        while(iter.hasNext()) {
            responseToClient += ("\"" + iter.next() + "\"");
            if(iter.hasNext()) {
                responseToClient += ',';
            }
        }

        return responseToClient + "]}";
    }

    public static String generateOKGetPlaylistInformation(Playlist playlist) {
        String responseForClient = RESPONSE_OK_SINGLE_PLAYLIST;

        // adding the playlist name to the response and setting up information for the songs
        responseForClient += playlist.getPlaylistName() + "\",\"songs\":[";

        // gets all the songs for the playlist
        Set<Song> allSongsForPlaylist = playlist.getSongs().keySet();
        Iterator<Song> songIterator = allSongsForPlaylist.iterator();

        while(songIterator.hasNext()) {
            Song song = songIterator.next();

            // adding the title
            responseForClient += "{\"title\":\"" + song.title() + "\",";
            responseForClient += "\"artist\":\"" + song.artist() + "\",";
            responseForClient += "\"duration\":\"" + song.duration() + "\",";
            responseForClient += "\"likes\":\"" + playlist.getLikesForSong(song) + "\"}";

            if(songIterator.hasNext()) {
                responseForClient += ",";
            }
        }

        responseForClient += "]}}";

        return responseForClient;
    }

    // when an unknown command is given to the command line
    public static String generateERRORUnknownCommand() {
        return UNKNOWN_COMMAND;
    }

    public static String generateErrorNotProperArgs(int expectedArgs) {
        return NOT_ENOUGH_ARGUMENTS + expectedArgs + "\"}";
    }

    public static String generateErrorExpectedCommand() {
        return RESPONSE_ERROR_EXPECTED_COMMAND;
    }
}
