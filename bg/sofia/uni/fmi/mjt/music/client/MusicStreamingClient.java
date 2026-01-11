package bg.sofia.uni.fmi.mjt.music.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class MusicStreamingClient {

    // the port and the ip to be connected to
    private final int port;
    private final String ip;

    // used in a loop to show that a communication is currently ongoing with the server
    private boolean activeConnectionWithServer;

    // the client is going to communicate through buffers with the server
    private static ByteBuffer byteBuffer;
    private static final int BUFFER_SIZE = 1024;

    // when the client uses this command the connection will be terminated
    private static final String STOP_CONNECTION = "disconnect";

    public MusicStreamingClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        activeConnectionWithServer = false;
        byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
    }

    public void connectToServer() {
        try (SocketChannel socketChannel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            // try to establish a connection with the server
            try {
                socketChannel.connect(new InetSocketAddress(ip, port));
            } catch (IOException e) {
                System.out.println("Problem with the connection to the server: " + e.getMessage());
                socketChannel.close();
                return;
            }

            // connection established
            activeConnectionWithServer = true;

            while (activeConnectionWithServer) {

                // String is read from the system in and sent to the server
                String dataToBeSent = scanner.nextLine();
                sendDataToServer(dataToBeSent, socketChannel);

                // if the client entered the command to disconnect, the connection is terminated
                if (dataToBeSent.equals(STOP_CONNECTION)) {
                    activeConnectionWithServer = false;
                    socketChannel.close();
                    break;
                }

                // receiving the data from the server and output to the client
                receiveDataFromServer(socketChannel);
            }

        } catch (IOException e) {
            System.out.println("There is a problem with the socket for the client " + e.getMessage());
        }
    }

    private void sendDataToServer(String dataToBeSent, SocketChannel socketChannel) {
        try {
            // position=0, limit=capacity, the buffer is prepared for writing
            byteBuffer.clear();
            // all the bytes from the string dataToBeSent are put into the buffer, the buffer position's is updated
            byteBuffer.put(dataToBeSent.getBytes(StandardCharsets.UTF_8));
            // limit=position,position=0, the buffer is prepared for reading
            byteBuffer.flip();
            // sending the buffer to the server
            socketChannel.write(byteBuffer);
        } catch (IOException e) {
            System.out.println("Problems with information written to the server: " + e.getMessage());
        }
    }

    private void receiveDataFromServer(SocketChannel socketChannel) {
        try {
            // position=0, limit=capacity, the buffer is prepared for writing
            byteBuffer.clear();
            // the buffer is received from the server
            socketChannel.read(byteBuffer);
            // limit=position, position=0, the buffer is prepared for reading
            byteBuffer.flip();

            // allocating enough space for the byte array, so that bytes from the buffer to be placed there
            byte[] byteArray = new byte[byteBuffer.remaining()];
            // transfer bytes from the buffer to the byte array, it takes every byte until the limit
            byteBuffer.get(byteArray);
            // from the byte array create a string
            String reply = new String(byteArray, StandardCharsets.UTF_8);
            // show the response to the client
            System.out.println(reply);
        } catch (IOException e) {
            System.out.println("Problems with information being received by the server: " + e.getMessage());
        }
    }

}
