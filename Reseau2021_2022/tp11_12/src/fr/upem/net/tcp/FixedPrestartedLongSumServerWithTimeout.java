package fr.upem.net.tcp;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import static fr.upem.net.tcp.ThreadData.TICK;

public class FixedPrestartedLongSumServerWithTimeout {
    private static final Logger logger = Logger.getLogger(FixedPrestartedLongSumServerWithTimeout.class.getName());
    private static final int BUFFER_SIZE = 1024;
    private static final int MAX_CLIENT = 4;
    private final ServerSocketChannel serverSocketChannel;
    private final int timeout;
    private static final ArrayList<ThreadData> threadDataList = new ArrayList<>(MAX_CLIENT);

    public FixedPrestartedLongSumServerWithTimeout(int port, int timeout) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        logger.info(this.getClass().getName() + " starts on port " + port);
        this.timeout = timeout;
    }

    /**
     * Iterative server main loop
     *
     * @throws IOException
     */

    public void launch() throws IOException, InterruptedException {
        logger.info("Server started");
        var data = new ThreadData();
        threadDataList.add(data);

        while (!Thread.interrupted()) {
            SocketChannel client = serverSocketChannel.accept();
            data.setSocketChannel(client);
            try {
                logger.info("Connection accepted from " + client.getRemoteAddress());
                serve(client);
                data.tick();
            } catch (AsynchronousCloseException e) {
                logger.info("Closed connection due to timeout");
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

    private static void silentlyClose(Closeable sc) {
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
        var server = new FixedPrestartedLongSumServerWithTimeout(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        var threadList = new ArrayList<Thread>();

        for (int i = 0; i < MAX_CLIENT; i++) {
            Thread thread = new Thread(() -> {
                try {
                    server.launch();
                } catch (InterruptedException e) {
                    logger.info("Interrupted server\n" + e);
                } catch (IOException ioe) {
                    logger.log(Level.SEVERE, "Server " + Thread.currentThread().getName() + "problem\n" + ioe);
                }
            });
            threadList.add(thread);
            thread.start();
        }

        var managerThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    for (var client : threadDataList) {
                        client.closeIfInactive(server.timeout);
                    }
                    Thread.sleep(TICK);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        managerThread.start();

        try (var scanner = new Scanner(System.in)) {
            int nbClientConnected;
            while (scanner.hasNextLine()) {
                switch (scanner.nextLine()) {
                    // GIVE THE NUMBER OF CLIENT CONNECTED TO THE SERVER
                    case "INFO" -> {
                        nbClientConnected = 0;
                        System.out.println("Number of client connected to a server");
                        for (ThreadData data : threadDataList) {
                            if (data.clientNotConnected()) {
                                nbClientConnected++;
                            }
                        }
                        System.out.println(nbClientConnected);
                    }
                    // STOP ACCESS TO THE SERVER
                    case "SHUTDOWN" -> {
                        silentlyClose(server.serverSocketChannel);
                        return;
                    }
                    // STOP EVERYTHING
                    case "SHUTDOWNNOW" -> {
                        for (Thread thread : threadList) {
                            thread.interrupt();
                        }
                        for (ThreadData data : threadDataList) {
                            data.close();
                        }
                        managerThread.interrupt();
                        return;
                    }
                    default -> System.out.println("Unknown command, try : INFO, SHUTDOWN or SHUTDOWNNOW");
                }
            }
        }
    }
}
