package bg.sofia.uni.fmi.mjt.music;

import bg.sofia.uni.fmi.mjt.music.server.MusicStreamingServer;

public class Main {

    public static void main(String[] args) {

        MusicStreamingServer musicStreamingServer = new MusicStreamingServer();
        musicStreamingServer.start();

    }
}
