package bg.sofia.uni.fmi.mjt.music.server.command;

import bg.sofia.uni.fmi.mjt.music.server.repository.PlaylistRepository;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.PlaylistAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.PlaylistNotFoundException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.SongAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.music.server.repository.exception.SongNotFoundException;

public class CommandExecutor {
    // the internal memory of the server
    private final PlaylistRepository playlistRepo;

    private static final int COMMAND_ARG = 0;
    private static final int PLAYLIST_INDEX = 1;
    private static final int SONG_INDEX = 2;
    private static final int ARTIST_INDEX = 3;
    private static final int DURATION_INDEX = 4;

    private static final int EXPECTED_ARGS_CREATION_PLAYLIST = 2;

    public CommandExecutor(PlaylistRepository playlistRepository) {
        this.playlistRepo = playlistRepository;
    }

    public String executeCommand(String command) {

        // depending on the command from the client, the CommandExecutor will perform functionality on the playlistRepo
        String[] commandArgs = command.split(" ");
        if (commandArgs.length == 0) {
            return ResponseGenerator.generateErrorExpectedCommand();
        }

        String responseForClient;
        switch (commandArgs[COMMAND_ARG]) {
            case "create-playlist" -> responseForClient = executeCreatePlaylist(commandArgs);
            case "add-song" -> responseForClient = executeAddSong(commandArgs);
            case "like-song", "unlike-song" -> responseForClient = executeLikeCountChange(commandArgs);
            case "list-playlists" -> responseForClient = executeGetAllPlaylists();
            case "get-playlist" -> responseForClient = executeGetPlaylist(commandArgs);
            default -> responseForClient = ResponseGenerator.generateERRORUnknownCommand();
        }

        return responseForClient;
    }

    private String executeCreatePlaylist(String[] commandArgs) {
        // there are only two expected args the command and the playlist name
        if (commandArgs.length != EXPECTED_ARGS_CREATION_PLAYLIST) {
            return ResponseGenerator.generateErrorNotProperArgs(EXPECTED_ARGS_CREATION_PLAYLIST);
        }

        try {
            String playlistName = commandArgs[PLAYLIST_INDEX];
            playlistRepo.createPlaylist(playlistName);
            return ResponseGenerator.generateOKPlaylistCreation(commandArgs[PLAYLIST_INDEX]);
        } catch (PlaylistAlreadyExistsException e) {
            return ResponseGenerator.generateERRORPlaylistCreation(commandArgs[PLAYLIST_INDEX]);
        }
    }

    private String executeAddSong(String[] commandArgs) {
        String playlistName = commandArgs[PLAYLIST_INDEX];
        String songTitle = commandArgs[SONG_INDEX];
        String artistName = commandArgs[ARTIST_INDEX];
        int duration;

        try {
            duration = Integer.parseInt(commandArgs[DURATION_INDEX]);
        } catch (NumberFormatException e) {
            return ResponseGenerator.generateErrorInvalidDurationForSong(commandArgs[DURATION_INDEX]);
        }

        try {
            playlistRepo.addSong(playlistName, songTitle, artistName, duration);
            return ResponseGenerator.generateOKPlaylistCreation(playlistName);
        } catch (IllegalArgumentException e) {
            return ResponseGenerator.generateErrorSongWithZeroDuration();
        } catch (PlaylistNotFoundException e) {
            return ResponseGenerator.generateMissingPlaylist(playlistName);
        } catch (SongAlreadyExistsException e) {
            return ResponseGenerator.generateErrorSongAlreadyExistsInPlaylist(playlistName, songTitle, artistName);
        }
    }

    private String executeLikeCountChange(String[] commandArgs) {
        String command = commandArgs[COMMAND_ARG];
        String playlistName = commandArgs[PLAYLIST_INDEX];
        String songTitle = commandArgs[SONG_INDEX];
        String artistName = commandArgs[ARTIST_INDEX];

        try {
            int newLikesCount;

            if (command.equals("like-song")) {
                newLikesCount = playlistRepo.likeSong(playlistName, songTitle, artistName);
            } else {
                newLikesCount = playlistRepo.unlikeSong(playlistName, songTitle, artistName);
            }

            return ResponseGenerator.generateOKSongLikeCountUpdated(songTitle, artistName, newLikesCount);
        } catch (PlaylistNotFoundException e) {
            return ResponseGenerator.generateMissingPlaylist(playlistName);
        } catch (SongNotFoundException e) {
            return ResponseGenerator.generateErrorSongNotFound(songTitle, artistName, playlistName);
        }
    }

    public String executeGetAllPlaylists() {
        return ResponseGenerator.generateOKAllPlaylist(playlistRepo.getAllPlaylists());
    }

    public String executeGetPlaylist(String[] commandArgs) {

        String playlistName = commandArgs[PLAYLIST_INDEX];

        try {
            return ResponseGenerator.generateOKGetPlaylistInformation(playlistRepo.getPlaylist(playlistName));
        } catch (PlaylistNotFoundException e) {
            return ResponseGenerator.generateMissingPlaylist(playlistName);
        }
    }
}
