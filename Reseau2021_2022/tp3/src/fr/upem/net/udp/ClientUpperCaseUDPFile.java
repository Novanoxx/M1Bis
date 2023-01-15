package fr.upem.net.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Logger;

import static java.nio.file.StandardOpenOption.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ClientUpperCaseUDPFile {
    private final static Charset UTF8 = StandardCharsets.UTF_8;
    private final static int BUFFER_SIZE = 1024;
    private static final Logger LOG = Logger.getLogger(ClientUpperCaseUDPRetry.class.getName());

    private static void usage() {
        System.out.println("Usage : ClientUpperCaseUDPFile in-filename out-filename timeout host port ");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 5) {
            usage();
            return;
        }

        var inFilename = args[0];
        var outFilename = args[1];
        var timeout = Integer.parseInt(args[2]);
        var server = new InetSocketAddress(args[3], Integer.parseInt(args[4]));
        var dc = DatagramChannel.open();
        dc.bind(null);
        var array_queue = new ArrayBlockingQueue<String>(1);
        var buffer = ByteBuffer.allocate(BUFFER_SIZE);
        var listener = new Thread(() -> {
            var buff_rec = ByteBuffer.allocate(BUFFER_SIZE);
            while(!Thread.interrupted()) {
                // Reception du buffer
                try {
                    var sender = (InetSocketAddress) dc.receive(buff_rec);
                    buff_rec.flip();
                    //LOG.info("Received " + buff_rec.remaining() + " bytes from " + sender + "\n");
                    var msg = UTF8.decode(buff_rec).toString();
                    array_queue.put(msg);
                    buff_rec.clear();
                } catch (Exception e) { // IOException | InterruptedException e
                    e.printStackTrace();
                }
            }
        });
        listener.start();

        // Read all lines of inFilename opened in UTF-8
        var lines = Files.readAllLines(Path.of(inFilename), UTF8);
        var upperCaseLines = new ArrayList<String>();

        for(var line : lines) {
            // Envoie du buffer
            buffer = UTF8.encode(line);
            dc.send(buffer, server);
            var msg = array_queue.poll(timeout, MILLISECONDS);
            while (msg == null) {
                LOG.warning("\n------------Le serveur n'a pas répondu------------\n");
                buffer.flip();
                dc.send(buffer, server);
                msg = array_queue.poll(timeout, MILLISECONDS);
                LOG.info("------------RETRY------------\n");
            }
            LOG.info("String received : " + msg + "\n");
            upperCaseLines.add(msg);
            buffer.clear();
        }
        // Write upperCaseLines to outFilename in UTF-8
        Files.write(Path.of(outFilename), upperCaseLines, UTF8, CREATE, WRITE, TRUNCATE_EXISTING);
        listener.interrupt();
        dc.close();
    }
}
/*
La présence de ligne dupliqué est dù au fait qu'un message jeté permet un renvoie du message et que puisqu'il y a un délai de 1 seconde et environ 2 ou 3 messages
renvoyés par le programme, il y a des lignes dupliquées.
 */