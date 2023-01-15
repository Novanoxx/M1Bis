package fr.upem.net.udp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardOpenOption.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ClientIdUpperCaseUDPOneByOne {

	private static Logger logger = Logger.getLogger(ClientIdUpperCaseUDPOneByOne.class.getName());
	private static final Charset UTF8 = StandardCharsets.UTF_8;
	private static final int BUFFER_SIZE = 1024;

	private record Response(long id, String message) {
	};

	private final String inFilename;
	private final String outFilename;
	private final long timeout;
	private final InetSocketAddress server;
	private final DatagramChannel dc;
	private final SynchronousQueue<Response> queue = new SynchronousQueue<>();

	public static void usage() {
		System.out.println("Usage : fr.upem.net.udp.ClientIdUpperCaseUDPOneByOne in-filename out-filename timeout host port ");
	}

	public ClientIdUpperCaseUDPOneByOne(String inFilename, String outFilename, long timeout, InetSocketAddress server)
			throws IOException {
		this.inFilename = Objects.requireNonNull(inFilename);
		this.outFilename = Objects.requireNonNull(outFilename);
		this.timeout = timeout;
		this.server = server;
		this.dc = DatagramChannel.open();
		dc.bind(null);
	}

	private void listenerThreadRun() {
		// Listener thread run
		var buff_rec = ByteBuffer.allocate(BUFFER_SIZE);
		while(!Thread.interrupted()) {
			try {
				dc.receive(buff_rec);
				buff_rec.flip();
				queue.put(new Response(buff_rec.getLong(), UTF8.decode(buff_rec).toString()));
				buff_rec.clear();
			} catch (AsynchronousCloseException | InterruptedException e) {
				logger.info("AsynchronousCloseException or InterruptedException");
				return;
			} catch (IOException e) {
				logger.log(Level.SEVERE, "IOException");
				return;
			}
		};
	}

	public void launch() throws IOException, InterruptedException {
		try {

			var listenerThread = new Thread(this::listenerThreadRun);
			listenerThread.start();

			// Read all lines of inFilename opened in UTF-8
			var lines = Files.readAllLines(Path.of(inFilename), UTF8);
			var buffer = ByteBuffer.allocate(BUFFER_SIZE);
			long check_id = 0L;
			var upperCaseLines = new ArrayList<String>();

			for(var line : lines) {
				buffer.putLong(check_id);
				buffer.put(UTF8.encode(line));
				dc.send(buffer.flip(), server);
				var msg = queue.poll(timeout, MILLISECONDS);
				long last_send = 0L;
				while (true) {
					//logger.info("\n------------Le serveur n'a pas répondu------------\n");
					var currentTime = System.currentTimeMillis();
					if (currentTime - last_send > timeout) {
						dc.send(buffer, server);
						last_send = currentTime;
						buffer.flip();
					}
					msg = queue.poll(last_send + timeout - currentTime, MILLISECONDS); // remaining time
					if (msg == null) {
						last_send = 0L;
						continue;
					}
					if (msg.id() == check_id) {
						break;
					}
 					//logger.info("------------RETRY------------\n");
				}
				check_id ++;
				logger.info(msg.message() + " reçu !");
				upperCaseLines.add(msg.message());
				buffer.clear();
			}

			listenerThread.interrupt();
			Files.write(Paths.get(outFilename), upperCaseLines, UTF8, CREATE, WRITE, TRUNCATE_EXISTING);
		} finally {
			dc.close();
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		if (args.length != 5) {
			usage();
			return;
		}

		var inFilename = args[0];
		var outFilename = args[1];
		var timeout = Long.parseLong(args[2]);
		var server = new InetSocketAddress(args[3], Integer.parseInt(args[4]));

		// Create client with the parameters and launch it
		new ClientIdUpperCaseUDPOneByOne(inFilename, outFilename, timeout, server).launch();
	}
}
