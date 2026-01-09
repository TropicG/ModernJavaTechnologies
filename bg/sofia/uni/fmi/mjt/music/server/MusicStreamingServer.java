package bg.sofia.uni.fmi.mjt.music.server;

import bg.sofia.uni.fmi.mjt.music.server.model.Playlist;
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

    private final int port = 5555;

    private static final int BUFFER_SIZE = 1024;

    private Selector selector;
    private ByteBuffer byteBuffer;

    private boolean isServerWorking = true;

    // Note: Dont forget to add the insideMemory
    //private final PlaylistRepository playlistRepository;
    //public MusicStreamingServer(int port, PlaylistRepository playlistRepository);

    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()){

            // binding the server to listen on this port and to not block
            serverSocketChannel.bind(new InetSocketAddress("localhost", port));
            serverSocketChannel.configureBlocking(false);

            // creating the selector and the server socket will be informed upon connecting with a new clinet
            selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            // this buffer is going to be used to communication with the clients
            this.byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);

            while (isServerWorking) {

                // checking if there are new clients ready to write information
                int readyChannels = selector.select();
                if(readyChannels < 0) {
                    System.out.println("Currently, no clients are requesting anything");
                    continue;
                }


                // getting all the keys
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> selectionKeyIter = selectedKeys.iterator();

                while(selectionKeyIter.hasNext()) {
                    SelectionKey currentKey = selectionKeyIter.next();

                    if(currentKey.isReadable()) {

                        SocketChannel clientChannel = (SocketChannel) currentKey.channel();

                        // gets the buffer reading for writing the new information
                        byteBuffer.clear();
                        int bytesRead = clientChannel.read(byteBuffer);
                        if(bytesRead < 0) {
                            System.out.println("The client has closed the connection");
                            clientChannel.close();
                            selectionKeyIter.remove();
                            continue;
                        }
                        byteBuffer.flip();
                        byte[] byteArray = new byte[byteBuffer.remaining()];
                        byteBuffer.get(byteArray);
                        byteBuffer.rewind();
                        String serverResponse = new String(byteArray, StandardCharsets.UTF_8);


                        if(serverResponse.equals("disconnect")) {
                            clientChannel.close();
                            selectionKeyIter.remove();

                        }


                        System.out.println("This is received by the server: " + serverResponse);
                        clientChannel.write(byteBuffer);

                    } else if (currentKey.isAcceptable()) {
                        ServerSocketChannel tempServerSocketChannel = (ServerSocketChannel) currentKey.channel();
                        SocketChannel clientChannel = tempServerSocketChannel.accept();
                        System.out.println("The clinet is connected to the server");

                        clientChannel.configureBlocking(false);
                        // the client will be registered and the server will be informed when it has data
                        clientChannel.register(selector, SelectionKey.OP_READ);
                    }

                    selectionKeyIter.remove();
                }
            }

        } catch (IOException e)  {
            System.out.println("Problems with setting up the server " + e.getMessage());
        }
    }

}
