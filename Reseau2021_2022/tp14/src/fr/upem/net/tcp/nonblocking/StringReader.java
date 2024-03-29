package fr.upem.net.tcp.nonblocking;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class StringReader implements Reader<String> {
    private enum State {
        DONE, WAITING, ERROR
    }

    private static int BUFFER_SIZE = 1024;
    private final ByteBuffer bufferSize = ByteBuffer.allocateDirect(Integer.BYTES);
    private final ByteBuffer bufferText = ByteBuffer.allocateDirect(BUFFER_SIZE);
    private String text;
    private State state = State.WAITING;

    @Override
    public ProcessStatus process(ByteBuffer bb) {
        if (state == State.DONE || state == State.ERROR) {
            throw new IllegalStateException();
        }
        bb.flip();
        try {
            while (bufferSize.hasRemaining() && bb.hasRemaining()) {
                bufferSize.put(bb.get());
            }
            // If not exactly an integer
            if (bufferSize.remaining() != 0) {
                return ProcessStatus.REFILL;
            }
            int size = bufferSize.flip().getInt();
            if (size < 0 || size > BUFFER_SIZE) {
                return ProcessStatus.ERROR;
            }
            while (bb.hasRemaining() && bufferText.position() < size && bufferText.hasRemaining()) {
                bufferText.put(bb.get());
            }
            if (bufferText.position() < size) {
                return ProcessStatus.REFILL;
            }
        } finally {
            bb.compact();
        }
        text = StandardCharsets.UTF_8.decode(bufferText.flip()).toString();
        state = State.DONE;
        return ProcessStatus.DONE;
    }

    @Override
    public String get() {
        if (state == State.DONE) {
            return text;
        }
        throw new IllegalStateException();
    }

    @Override
    public void reset() {
        state = State.WAITING;
        bufferSize.clear();
        bufferText.clear();
    }
}
