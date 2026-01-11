package bg.sofia.uni.fmi.mjt.music.server;

import bg.sofia.uni.fmi.mjt.music.server.command.CommandExecutor;
import bg.sofia.uni.fmi.mjt.music.server.repository.PlaylistRepository;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class MusicStreamingServer {

    private final String ip = "localhost";
    private final int port;
    private final CommandExecutor commandExecutor;

    private static final int BUFFER_SIZE = 1024;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);

    private Selector selector;

    private boolean isServerWorking = true;

    private static final String DISCONNECT = "disconnect";

    public MusicStreamingServer(int port, PlaylistRepository inMemoryPlaylistRepo) {
        this.port = port;
        this.commandExecutor = new CommandExecutor(inMemoryPlaylistRepo);
    }

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            setupServer(serverSocketChannel);
            setupSelector(serverSocketChannel);

            // this buffer is going to be used to communication with the clients
            this.byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (isServerWorking) {

                // checking if there are new clients ready to write information
                int readyChannels = selector.select();
                if (readyChannels < 0) {
                    System.out.println("Currently, no clients are requesting anything");
                    continue;
                }

                // getting all the keys
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIter = selectedKeys.iterator();

                while (selectionKeyIter.hasNext()) {
                    SelectionKey currentKey = selectionKeyIter.next();

                    if (currentKey.isReadable()) {
                        SocketChannel clientChannel = (SocketChannel) currentKey.channel();

                        int bytesRead = readDataFromTheClient(clientChannel);
                        if (bytesRead < 0) {
                            System.out.println("The client has closed the connection");
                            clientChannel.close();
                            selectionKeyIter.remove();
                            continue;
                        }

                        String clientRequest = getRequestFromClient();
                        if (clientRequest.equals(DISCONNECT)) {
                            clientChannel.close();
                            selectionKeyIter.remove();
                            continue;
                        }

                        // the command is going to be executed from here
                        String serverResponse = commandExecutor.executeCommand(clientRequest);
                        sendDataToTheClient(clientChannel, serverResponse);

                    } else if (currentKey.isAcceptable()) {
                        ServerSocketChannel tempServerSocketChannel = (ServerSocketChannel) currentKey.channel();
                        acceptNewClient(tempServerSocketChannel);
                    }

                    selectionKeyIter.remove();
                }
            }

        } catch (IOException e) {
            System.out.println("Problems with setting up the server " + e.getMessage());
        }
    }

    private void setupServer(ServerSocketChannel serverSocketChannel) throws IOException {
        // binding the server to listen on this port and to not block
        serverSocketChannel.bind(new InetSocketAddress("localhost", port));
        serverSocketChannel.configureBlocking(false);

        // creating the selector and the server socket will be informed upon connecting with a new clinet
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void setupSelector(ServerSocketChannel serverSocketChannel) throws IOException {
        // creating the selector and the server socket will be informed upon connecting with a new clinet
        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void sendDataToTheClient(SocketChannel clientChannel, String serverResponse) {
        try {
            // position=0, limit=capacity, prepares the buffer for writing mode
            byteBuffer.clear();
            // puts in the buffer the content of the server response in bytes for the client
            byteBuffer.put(serverResponse.getBytes(StandardCharsets.UTF_8));
            // limit=position, position=0, prepares the buffer for reading mode
            byteBuffer.flip();
            // writing information to the client
            clientChannel.write(byteBuffer);
        } catch (IOException e) {
            System.out.println("There is a problem with writing information to the client");
        }
    }

    private int readDataFromTheClient(SocketChannel clientChannel) {
        int bytesRead = 0;
        try {
            // gets the buffer reading for writing the new information
            byteBuffer.clear();
            bytesRead = clientChannel.read(byteBuffer);
            byteBuffer.flip();
        } catch (IOException e) {
            System.out.println("There is a problem with reading information from the client");
        }
        return bytesRead;
    }

    private String getRequestFromClient() {
        byte[] byteArray = new byte[byteBuffer.remaining()];
        byteBuffer.get(byteArray);
        return new String(byteArray, StandardCharsets.UTF_8);
    }

    private void acceptNewClient(ServerSocketChannel tempServerSocketChannel) {
        try {
            SocketChannel clientChannel = tempServerSocketChannel.accept();
            System.out.println("The clinet is connected to the server");

            clientChannel.configureBlocking(false);
            // the client will be registered and the server will be informed when it has data
            clientChannel.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            System.out.println("Problem with accepting the new client: " + e.getMessage());
        }
    }
}
