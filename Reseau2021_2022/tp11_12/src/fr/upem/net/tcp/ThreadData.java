package fr.upem.net.tcp;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class ThreadData {
    private SocketChannel client;
    private int time;
    public static final int TICK = 5;
    private final Object locker = new Object();

    void setSocketChannel(SocketChannel client) {
        synchronized (locker) {
            this.client = client;
            this.time = 0;
        }
    }

    boolean clientNotConnected() {
        synchronized (locker) {
            return client != null;
        }
    }

    void tick() {
        synchronized (locker) {
            time = 0;
        }
    }

    void closeIfInactive(int timeout){
        synchronized (locker) {
            if (client == null) {
                return;
            }
            try {
                if (time > timeout / TICK) {
                    close();
                } else {
                    time++;
                }
            } catch (IOException ioe) {
                // do nothng
            }
        }
    }

    void close() throws IOException {
        synchronized (locker) {
            client.close();
        }
    }
}
