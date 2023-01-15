package fr.upem.net.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class NetcatUDP {
    public static final int BUFFER_SIZE = 1024;

    private static void usage() {
        System.out.println("Usage : NetcatUDP host port charset");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            usage();
            return;
        }

        var server = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
        var cs = Charset.forName(args[2]);
        var buffer = ByteBuffer.allocate(BUFFER_SIZE);
        //Creation de la boite aux lettre
        var dc = DatagramChannel.open();
        dc.bind(null);

        try (var scanner = new Scanner(System.in);) {
            while (scanner.hasNextLine()) {
                var line = scanner.nextLine();
                // Envoie du buffer
                buffer = cs.encode(line);
                dc.send(buffer, server);
                buffer.clear();
                // Reception du buffer
                var buff_rec = ByteBuffer.allocate(BUFFER_SIZE);
                var sender = (InetSocketAddress) dc.receive(buff_rec);
                buff_rec.flip();
                System.out.println("Received " + buff_rec.remaining() + " bytes from " + sender);
                System.out.println("String : " + cs.decode(buff_rec));
            }
        }
        dc.close();
    }
}
