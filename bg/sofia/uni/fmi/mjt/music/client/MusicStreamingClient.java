package bg.sofia.uni.fmi.mjt.music.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MusicStreamingClient {

    private static final int port = 5555;
    private static final String ip = "localhost";

    private static ByteBuffer byteBuffer;

    public static void main(String[] args) {

        byteBuffer = ByteBuffer.allocate(1024);

        try(SocketChannel socketChannel = SocketChannel.open();
            Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(ip, port));

            System.out.println("Connected to the server");

            while(true) {

                System.out.println("Write information to be sent to the server: ");
                String inputLine = scanner.nextLine();

                System.out.println("Client request: " + inputLine);

                // the gotten information from System.in will be read and put into the byteBuffer and be sent to the server
                byteBuffer.clear();
                byteBuffer.put(inputLine.getBytes(StandardCharsets.UTF_8));
                byteBuffer.flip();
                socketChannel.write(byteBuffer);

                if(inputLine.equals("disconnect")) {
                    break;
                }

                System.out.println("Information sent to the server");

                // the byte is being cleaned and read from the server
                byteBuffer.clear();
                socketChannel.read(byteBuffer);
                // it is fliiped again to be read from the client
                byteBuffer.flip();

                // the send information is read in bytes and after that transfered
                byte[] byteArray = new byte[byteBuffer.remaining()];
                byteBuffer.get(byteArray);
                String reply = new String(byteArray, StandardCharsets.UTF_8);
                System.out.println("Reply from the server " + inputLine);
            }

        } catch (IOException e) {
            System.out.println("There is a problem with the network " + e.getMessage());
        }

    }

}
