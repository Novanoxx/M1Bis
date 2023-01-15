package fr.upem.net.udp;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.logging.Logger;

public class ServerLongSum {
    private static final Logger logger = Logger.getLogger(ServerIdUpperCaseUDP.class.getName());
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final int BUFFER_SIZE = 1024;

    private final DatagramChannel dc;
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private final HashMap<PairID, Data> map = new HashMap<>();

    private record PairID(InetSocketAddress ip, long sessionID) {}

    public ServerLongSum(int port) throws IOException {
        dc = DatagramChannel.open();
        dc.bind(new InetSocketAddress(port));
        logger.info("ServerBetterUpperCaseUDP started on port " + port);
    }

    public void serve() throws IOException {
        try {
            var buff_send = ByteBuffer.allocateDirect(BUFFER_SIZE);
            while (!Thread.interrupted()) {
                buffer.clear();
                var sender = (InetSocketAddress) dc.receive(buffer);
                buffer.flip();

                var type = buffer.get();
                var sessionID = buffer.getLong();
                var idPosOper = buffer.getLong();
                var totalOper = buffer.getLong();
                var opValue = buffer.getLong();

                var pair = new PairID(sender, sessionID);

                // Receive OP pack
                map.putIfAbsent(pair, new Data(totalOper));
                var data = map.get(pair);
                data.addSum((int) idPosOper, opValue);
                map.put(pair, data);

                // Send ACK pack
                buff_send.clear();
                buff_send.put((byte) 2).putLong(sessionID).putLong(idPosOper);
                dc.send(buff_send.flip(), sender);

                // Send RES pack
                if (map.get(pair).isFinished()) {
                    buff_send.clear();
                    buff_send.put((byte) 3).putLong(sessionID).putLong(map.get(pair).sum());
                    dc.send(buff_send.flip(), sender);
                }
            }
        } finally {
            dc.close();
        }
    }

    public static void usage() {
        System.out.println("Usage : ServerLongSum port");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            usage();
            return;
        }

        var port = Integer.parseInt(args[0]);

        if (!(port >= 1024) & port <= 65535) {
            logger.severe("The port number must be between 1024 and 65535");
            return;
        }

        try {
            new ServerLongSum(port).serve();
        } catch (BindException e) {
            logger.severe("Server could not bind on " + port + "\nAnother server is probably running on this port.");
            return;
        }
    }
}
