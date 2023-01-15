package fr.upem.net.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Logger;

public class ClientConcatenation {
    public static final Logger logger = Logger.getLogger(ClientConcatenation.class.getName());
    private static final ArrayList<String> lst = new ArrayList<>();

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

    private static String getMessage(SocketChannel sc) throws IOException {
        var sendBuffer = ByteBuffer.allocate(1024);
        sendBuffer.putInt(lst.size());

        for(var elem : lst) {
            sendBuffer.putInt(elem.getBytes(StandardCharsets.UTF_8).length);
            sendBuffer.put(StandardCharsets.UTF_8.encode(elem));
        }
        sc.write(sendBuffer.flip());

        var receiveSize = ByteBuffer.allocate(Integer.BYTES);
        if (!readFully(sc, receiveSize)) {
            return null;
        }
        var sizeBuffer = receiveSize.getInt();
        if (sizeBuffer < Integer.BYTES) {
            logger.warning("Wrong type of packet");
            return null;
        }

        var msgBuffer = ByteBuffer.allocate(sizeBuffer);
        readFully(sc, msgBuffer);
        return StandardCharsets.UTF_8.decode(msgBuffer).toString();
    }

    public static void main(String[] args) throws IOException {
        var server = new InetSocketAddress(args[0], Integer.parseInt(args[1]));

        try (var sc = SocketChannel.open(server)) {
            var scan = new Scanner(System.in);
            String line;
            while(true) {
                do {
                    line = scan.nextLine();
                    if (line.isEmpty()) {
                        break;
                    }
                    lst.add(line);
                } while (true);

                var msg = getMessage(sc);
                if (msg == null) {
                    logger.warning("Connection with server lost.");
                    return;
                }
                System.out.println(msg);
                lst.clear();
            }
        }
    }
}
