package bg.sofia.uni.fmi.mjt.music;

import bg.sofia.uni.fmi.mjt.music.client.MusicStreamingClient;
import bg.sofia.uni.fmi.mjt.music.server.MusicStreamingServer;
import bg.sofia.uni.fmi.mjt.music.server.repository.InMemoryPlaylistRepository;

public class MainServer {

    public static void main(String[] args) {

        MusicStreamingServer musicStreamingServer = new MusicStreamingServer(5555, new InMemoryPlaylistRepository());
        musicStreamingServer.start();
    }
}
