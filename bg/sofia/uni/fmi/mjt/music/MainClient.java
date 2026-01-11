package bg.sofia.uni.fmi.mjt.music;

import bg.sofia.uni.fmi.mjt.music.client.MusicStreamingClient;

public class MainClient {

    public static void main(String[] args) {

        MusicStreamingClient musicStreamingClient = new MusicStreamingClient("localhost", 5555);
        musicStreamingClient.connectToServer();

    }
}
