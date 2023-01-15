package fr.upem.net.tcp.nonblocking;

import java.nio.ByteBuffer;

public class MessageReader implements Reader<Message>{
    private enum State {
        DONE, WAITING, ERROR
    }

    private Message msg;
    private State state = State.WAITING;
    private final StringReader reader = new StringReader();

    @Override
    public ProcessStatus process(ByteBuffer bb) {
        var readerState = reader.process(bb);
        if (readerState == ProcessStatus.DONE) {
            var login = reader.get();
            reader.reset();
            readerState = reader.process(bb);
            if (readerState == ProcessStatus.DONE) {
                var text = reader.get();
                msg = new Message(login, text);
            } else {
                return readerState;
            }
        } else {
            return readerState;
        }
        state = State.DONE;
        return ProcessStatus.DONE;
    }

    @Override
    public Message get() {
        if (state == State.DONE) {
            return msg;
        }
        throw new IllegalStateException();
    }

    @Override
    public void reset() {
        state = State.WAITING;
        reader.reset();
    }
}
