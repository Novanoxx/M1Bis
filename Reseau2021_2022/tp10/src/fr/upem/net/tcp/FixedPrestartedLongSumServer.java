package fr.upem.net.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FixedPrestartedLongSumServer {
    private static final Logger logger = Logger.getLogger(FixedPrestartedLongSumServer.class.getName());
    private static final int BUFFER_SIZE = 1024;
    private final ServerSocketChannel serverSocketChannel;
    private static final int MAX_CLIENT = 4;

    public FixedPrestartedLongSumServer(int port) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        logger.info(this.getClass().getName() + " starts on port " + port);
    }

    /**
     * Iterative server main loop
     *
     * @throws IOException
     */

    public void launch() throws IOException, InterruptedException {
        logger.info("Server started");

        while (!Thread.interrupted()) {
            SocketChannel client = serverSocketChannel.accept();
            try {
                logger.info("Connection accepted from " + client.getRemoteAddress());
                serve(client);
            } catch (IOException ioe) {
                logger.log(Level.SEVERE, "Connection terminated with client by IOException", ioe.getCause());
            } finally {
                silentlyClose(client);
            }
        }
    }

    /**
     * Treat the connection sc applying the protocol. All IOException are thrown
     *
     * @param sc
     * @throws IOException
     */
    private void serve(SocketChannel sc) throws IOException {
        while(true) {
            var receiveBuffer = ByteBuffer.allocate(Integer.BYTES);
            long result = 0L;
            if (!readFully(sc, receiveBuffer)) {
                return;
            }
            receiveBuffer.flip();
            var numberOp = receiveBuffer.getInt();

            receiveBuffer = ByteBuffer.allocate(Long.BYTES * numberOp);
            if (!readFully(sc, receiveBuffer)) {
                return;
            }
            receiveBuffer.flip();
            for (int i = 0; i < numberOp; i++) {
                result += receiveBuffer.getLong();
            }
            var sendBuffer = ByteBuffer.allocate(Long.BYTES);
            sendBuffer.putLong(result);
            sc.write(sendBuffer.flip());
        }
    }

    /**
     * Close a SocketChannel while ignoring IOException
     *
     * @param sc
     */

    private void silentlyClose(Closeable sc) {
        if (sc != null) {
            try {
                sc.close();
            } catch (IOException e) {
                // Do nothing
            }
        }
    }

    static boolean readFully(SocketChannel sc, ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            if (sc.read(buffer) == -1) {
                logger.info("Input stream closed");
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException {
        var server = new FixedPrestartedLongSumServer(Integer.parseInt(args[0]));

        for (int i = 0; i < Integer.parseInt(args[1]); i++) {
            new Thread(() -> {
                try {
                    server.launch();
                } catch (InterruptedException e) {
                    logger.info("Interrupted server\n" + e);
                } catch (IOException ioe) {
                    logger.log(Level.SEVERE, "Server " + Thread.currentThread().getName() + "problem\n" + ioe);
                }
            }).start();
        }
    }
}
