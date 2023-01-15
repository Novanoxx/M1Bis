package fr.upem.net.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientIdUpperCaseUDPBurst {

        private static Logger logger = Logger.getLogger(ClientIdUpperCaseUDPBurst.class.getName());
        private static final Charset UTF8 = StandardCharsets.UTF_8;
        private static final int BUFFER_SIZE = 1024;
        private final List<String> lines;
        private final int nbLines;
        private final String[] upperCaseLines; //
        private final int timeout;
        private final String outFilename;
        private final InetSocketAddress serverAddress;
        private final DatagramChannel dc;
        private final AnswersLog answersLog;         // Thread-safe structure keeping track of missing responses

        public static void usage() {
            System.out.println("Usage : ClientIdUpperCaseUDPBurst in-filename out-filename timeout host port ");
        }

        public ClientIdUpperCaseUDPBurst(List<String> lines,int timeout,InetSocketAddress serverAddress,String outFilename) throws IOException {
            this.lines = lines;
            this.nbLines = lines.size();
            this.timeout = timeout;
            this.outFilename = outFilename;
            this.serverAddress = serverAddress;
            this.dc = DatagramChannel.open();
            dc.bind(null);
            this.upperCaseLines = new String[nbLines];
            this.answersLog = new AnswersLog(nbLines);
        }

        private void senderThreadRun() {
            var buff_rec = ByteBuffer.allocate(BUFFER_SIZE);

            while (answersLog.isMissingAnswers()) {
                var currentLine = 0;
                for (var line : lines) {
                    if (!answersLog.getAnswer(currentLine)) {
                        buff_rec.putLong(currentLine);
                        buff_rec.put(UTF8.encode(line));
                        buff_rec.flip();
                        try {
                            dc.send(buff_rec, serverAddress);
                        } catch (AsynchronousCloseException e) {
                            logger.info("AsynchronousCloseException");
                            return;
                        } catch (IOException e) {
                            logger.log(Level.SEVERE, "IOException : sending message");
                            return;
                        }

                    }
                    currentLine++;
                    buff_rec.clear();
                }

                // Pseudo wait()
                var timeSend = System.currentTimeMillis();
                while (System.currentTimeMillis() - timeSend <= timeout) {
                }
            }

        }

        public void launch() throws IOException {
            Thread senderThread = new Thread(this::senderThreadRun);
            senderThread.start();

            var buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (answersLog.isMissingAnswers()) {
                try {
                    dc.receive(buffer);
                    buffer.flip();
                    var answerLine = (int) buffer.getLong();
                    if (!answersLog.getAnswer(answerLine)) {
                        answersLog.setAnswer(answerLine);
                        upperCaseLines[answerLine] = UTF8.decode(buffer).toString();
                    }
                    buffer.clear();
                }
                catch (AsynchronousCloseException e) {
                    logger.info("listenerThread interrupted");
                    return;
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "IOException : waiting on receive");
                    return;
                }
            }
            senderThread.interrupt();
				
				Files.write(Paths.get(outFilename),Arrays.asList(upperCaseLines), UTF8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.WRITE,
                    StandardOpenOption.TRUNCATE_EXISTING);

        }

        public static void main(String[] args) throws IOException {
            if (args.length !=5) {
                usage();
                return;
            }

            String inFilename = args[0];
            String outFilename = args[1];
            int timeout = Integer.valueOf(args[2]);
            String host=args[3];
            int port = Integer.valueOf(args[4]);
            InetSocketAddress serverAddress = new InetSocketAddress(host,port);

            //Read all lines of inFilename opened in UTF-8
            List<String> lines= Files.readAllLines(Paths.get(inFilename),UTF8);
            //Create client with the parameters and launch it
            ClientIdUpperCaseUDPBurst client = new ClientIdUpperCaseUDPBurst(lines,timeout,serverAddress,outFilename);
            client.launch();

        }

        private static class AnswersLog {
            private final BitSet answers;
            private final int nbLines;

            public AnswersLog(int nbLines) {
                this.answers = new BitSet(nbLines);
                this.nbLines = nbLines;
            }

            boolean isMissingAnswers() {
                synchronized (answers) {
                    return answers.cardinality() != nbLines;
                }
            }

            void setAnswer(int lineNumber) {
                synchronized (answers) {
                    answers.set(lineNumber);
                }
            }

            boolean getAnswer(int lineNumber) {
                synchronized (answers) {
                    return answers.get(lineNumber);
                }
            }

        }
    }


