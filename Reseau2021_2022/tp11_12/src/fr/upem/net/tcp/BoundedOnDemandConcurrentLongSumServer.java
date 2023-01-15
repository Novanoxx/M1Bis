package fr.upem.net.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BoundedOnDemandConcurrentLongSumServer {
    private static final Logger logger = Logger.getLogger(BoundedOnDemandConcurrentLongSumServer.class.getName());
    private static final int BUFFER_SIZE = 1024;
    private final ServerSocketChannel serverSocketChannel;
    private final int maxClient;

    public BoundedOnDemandConcurrentLongSumServer(int port, String maxClient) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        logger.info(this.getClass().getName() + " starts on port " + port);
        this.maxClient = Integer.parseInt(maxClient);
    }

    /**
     * Iterative server main loop
     *
     * @throws IOException
     */

    public void launch() throws IOException, InterruptedException {
        logger.info("Server started");
        Semaphore semaphore = new Semaphore(maxClient);
        while (!Thread.interrupted()) {
            semaphore.acquire();
            SocketChannel client = serverSocketChannel.accept();
            new Thread(() -> {
                try {
                    logger.info("Connection accepted from " + client.getRemoteAddress());
                    serve(client);
                } catch (IOException ioe) {
                    logger.log(Level.SEVERE, "Connection terminated with client by IOException", ioe.getCause());
                } finally {
                    silentlyClose(client);
                    semaphore.release();
                }
            }).start();
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
     * Close a SocketChannel while ignoring IOExecption
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
        var server = new BoundedOnDemandConcurrentLongSumServer(Integer.parseInt(args[0]), args[1]);
        server.launch();
    }
}
