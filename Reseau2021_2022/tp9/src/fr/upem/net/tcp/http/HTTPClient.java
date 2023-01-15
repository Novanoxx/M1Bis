package fr.upem.net.tcp.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class HTTPClient {
    private final InetSocketAddress server;
    private final String request;

    public HTTPClient(InetSocketAddress server, String request) {
        this.server = server;
        this.request = request;
    }

    public static void main(String[] args) throws IOException {
        /*
        var client = new HTTPClient(
                new InetSocketAddress("www.w3.org", 80),
                "GET / HTTP/1.1\r\n" + "Host: www.w3.org\r\n" + "\r\n"
        );
        */
        var client = new HTTPClient(
                new InetSocketAddress("www-igm.univ-mlv.fr", 80),
                "GET /~carayol/redirect.php HTTP/1.1\r\nHost: www-igm.univ-mlv.fr\r\n\r\n");

        var charsetASCII = StandardCharsets.US_ASCII;
        var sc = SocketChannel.open();
        sc.connect(client.server);
        sc.write(charsetASCII.encode(client.request));

        var buffer = ByteBuffer.allocate(50);
        var reader = new HTTPReader(sc, buffer);

        var header = reader.readHeader();
        var checkType = header.getContentType();    // getContentLength
        if (checkType.isEmpty()) {
            return;
        }
        var type = checkType.get().split("/")[1];   // checkType is text/html
        var codeNum = header.getCode();
        if (codeNum == 301 || codeNum == 302) {
            var location = header.getFields().get("location");
            // je ne comprends pas quoi faire apres
        }

        if (codeNum == 200) {
            if (type.equals("html")) {
                System.out.println(header);
                var body = reader.readBytes(header.getContentLength());
                body.flip();
                System.out.println(header.getCharset().orElse(StandardCharsets.UTF_8).decode(body));
            }
        }

        sc.close();
    }
}
