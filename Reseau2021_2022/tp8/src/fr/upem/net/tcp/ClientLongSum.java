package fr.upem.net.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.logging.Logger;

public class ClientLongSum {

    public static final Logger logger = Logger.getLogger(ClientLongSum.class.getName());

    private static List<Long> randomLongList(int size) {
        return new Random().longs(size).boxed().toList();
    }

    private static boolean checkSum(List<Long> list, long response) {
        return list.stream().reduce(Long::sum).orElse(0L) == response;
    }

    /**
     * Write all the longs in list in BigEndian on the server and read the long sent
     * by the server and returns it
     *
     * returns Optional.empty if the protocol is not followed by the server but no
     * IOException is thrown
     *
     * @param sc
     * @param list
     * @return
     * @throws IOException
     */
    private static Optional<Long> requestSumForList(SocketChannel sc, List<Long> list) throws IOException {
        var sendBuffer = ByteBuffer.allocate(1024);
        sendBuffer.putInt(list.size());
        for (var value : list) {
            sendBuffer.putLong(value);
        }
        sc.write(sendBuffer.flip());

        var receiveBuffer = ByteBuffer.allocate(Long.BYTES);
        if (!readFully(sc, receiveBuffer)) {
            return Optional.empty();
        }
        return Optional.of(receiveBuffer.getLong());
    }

    /**
     * Fill the workspace of the Bytebuffer with bytes read from sc.
     *
     * @param sc
     * @param buffer
     * @return false if read returned -1 at some point and true otherwise
     * @throws IOException
     */
    static boolean readFully(SocketChannel sc, ByteBuffer buffer) throws IOException {
        while (buffer.hasRemaining()) {
            if (sc.read(buffer) == -1) {
                logger.info("Connection closed for reading");
                return false;
            }
        }
        buffer.flip();
        return true;
    }

    public static void main(String[] args) throws IOException {
        var server = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
        try (var sc = SocketChannel.open(server)) {
            for (var i = 0; i < 5; i++) {
                var list = randomLongList(50);

                var sum = requestSumForList(sc, list);
                if (!sum.isPresent()) {
                    logger.warning("Connection with server lost.");
                    return;
                }
                if (!checkSum(list, sum.get())) {
                    logger.warning("Oups! Something wrong happened!");
                }
            }
            logger.info("Everything seems ok");
        }
    }
}