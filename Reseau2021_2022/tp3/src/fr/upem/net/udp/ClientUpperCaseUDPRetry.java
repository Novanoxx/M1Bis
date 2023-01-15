package fr.upem.net.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ClientUpperCaseUDPRetry {
    public static final int BUFFER_SIZE = 1024;
    private static final Logger LOG = Logger.getLogger(ClientUpperCaseUDPRetry.class.getName());

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
        var array_queue = new ArrayBlockingQueue<String>(1);

        var listener = new Thread(() -> {
            var buff_rec = ByteBuffer.allocate(BUFFER_SIZE);
            while(!Thread.currentThread().isInterrupted()) {
                // Reception du buffer
                try {
                    var sender = (InetSocketAddress) dc.receive(buff_rec);
                    buff_rec.flip();
                    LOG.info("Received " + buff_rec.remaining() + " bytes from " + sender + "\n");
                    var msg = cs.decode(buff_rec).toString();
                    array_queue.put(msg);
                    buff_rec.clear();
                } catch (Exception e) { // IOException | InterruptedException e
                    e.printStackTrace();
                }
            }
        });
        listener.start();

        try (var scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                var line = scanner.nextLine();
                // Envoie du buffer
                buffer = cs.encode(line);
                dc.send(buffer, server);
                var msg = array_queue.poll(1000, MILLISECONDS);
                while (msg == null) {
                   LOG.warning("\n------------Le serveur n'a pas répondu------------\n");
                   buffer.flip();
                   dc.send(buffer, server);
                   msg = array_queue.poll(1000, MILLISECONDS);
                   LOG.info("------------RETRY------------\n");
                }
                LOG.info("String received : " + msg + "\n");
                buffer.clear();
            }
        } catch (Exception e) {
            LOG.warning("\n------------PROBLEME------------\n");
        }
        listener.interrupt();
        dc.close();
    }
}
