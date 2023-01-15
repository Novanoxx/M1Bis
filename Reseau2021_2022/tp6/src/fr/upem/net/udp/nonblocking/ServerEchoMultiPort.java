package fr.upem.net.udp.nonblocking;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.logging.Logger;

public class ServerEchoMultiPort {
    private static final Logger logger = Logger.getLogger(ServerEchoMultiPort.class.getName());

    private final Selector selector;
    private final int BUFFER_SIZE = 1024;
    private final int portBegin;
    private final int portEnd;

    private class Context {
        private final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
        private InetSocketAddress sender;
    }

    public ServerEchoMultiPort(int portBegin, int portEnd) throws IOException {
        this.portBegin = portBegin;
        this.portEnd = portEnd;
        selector = Selector.open();
        for (var port = portBegin; port < portEnd; port++) {
            var dc = DatagramChannel.open();
            var socket = new InetSocketAddress(port);
            dc.bind(socket);
            dc.configureBlocking(false);
            dc.register(selector, SelectionKey.OP_READ, new Context());
        }
    }

    public void serve() throws IOException {
        logger.info("ServerEcho started on port " + portBegin + " to port " + portEnd);
        while (!Thread.interrupted()) {
            try {
                selector.select(this::treatKey);
            } catch (UncheckedIOException uio) {
                throw uio.getCause();
            }
        }
    }

    private void treatKey(SelectionKey key) {
        try {
            if (key.isValid() && key.isWritable()) {
                doWrite(key);
            }
            if (key.isValid() && key.isReadable()) {
                doRead(key);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

    }

    private void doRead(SelectionKey key) throws IOException {
        var context = (Context) key.attachment();
        context.buffer.clear();
        var datagram = (DatagramChannel) key.channel();
        var sender = (InetSocketAddress) datagram.receive(context.buffer);

        if (sender == null) {
            logger.info("Le selecteur ment");
            return;
        }
        context.buffer.flip();
        context.sender = sender;
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void doWrite(SelectionKey key) throws IOException {
        var context = (Context) key.attachment();
        var datagram = (DatagramChannel) key.channel();
        datagram.send(context.buffer, context.sender);
        if (context.buffer.hasRemaining()) {
            logger.info("Il ment encore");
            return;
        }
        key.interestOps(SelectionKey.OP_READ);
    }

    public static void usage() {
        System.out.println("Usage : ServerEcho port");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            usage();
            return;
        }
        new ServerEchoMultiPort(Integer.parseInt(args[0]), Integer.parseInt(args[1])).serve();
    }
}
