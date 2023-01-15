package fr.upem.net.tcp.http;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class HTTPReader {

    private final Charset ASCII_CHARSET = Charset.forName("ASCII");
    private final SocketChannel sc;
    private final ByteBuffer buffer;

    public HTTPReader(SocketChannel sc, ByteBuffer buffer) {
        this.sc = sc;
        this.buffer = buffer;
    }

    /**
     * @return The ASCII string terminated by CRLF without the CRLF
     *         <p>
     *         The method assume that buffer is in write mode and leaves it in
     *         write mode The method process the data from the buffer and if necessary
     *         will read more data from the socket.
     * @throws IOException HTTPException if the connection is closed before a line
     *                     could be read
     */
    public String readLineCRLF() throws IOException {
        buffer.flip();
        try {
            StringBuilder builder = new StringBuilder();
            while (true) {
                if (!buffer.hasRemaining()) {   // If buffer has no place
                    buffer.clear();
                    if (sc.read(buffer) == -1) {
                        throw new HTTPException();
                    }
                    buffer.flip();
                } else {
                    var before = (char) buffer.get();
                    builder.append(before);
                    char current;
                    while(buffer.hasRemaining()) {
                        current = (char) buffer.get();
                        if (before == '\r' && current == '\n') {
                            builder.delete(builder.length()-1, builder.length());
                            return builder.toString();
                        }
                        builder.append(current);
                        before = current;
                    }
                }
            }
        } finally {
            buffer.compact();
        }
    }

    /**
     * @return The HTTPHeader object corresponding to the header read
     * @throws IOException HTTPException if the connection is closed before a header
     *                     could be read or if the header is ill-formed
     */
    public HTTPHeader readHeader() throws IOException {
        var header = readLineCRLF();
        var line = readLineCRLF();
        var map = new HashMap<String, String>();
        while (!line.equals("")) {
            var tmp = line.split(": ");
            map.computeIfAbsent(tmp[0], key -> Arrays.stream(tmp).skip(1).collect(Collectors.joining()));
            line = readLineCRLF();
        }
        return HTTPHeader.create(header, map);
    }

    /**
     * @param size 
     * @return a ByteBuffer in write mode containing size bytes read on the socket
     *         <p>
     *         The method assume that buffer is in write mode and leaves it in
     *         write mode The method process the data from the buffer and if necessary
     *         will read more data from the socket.
     * @throws IOException HTTPException is the connection is closed before all
     *                     bytes could be read
     */
    public ByteBuffer readBytes(int size) throws IOException {
        buffer.flip();
        var resultBuffer = ByteBuffer.allocate(size);
        try {
            while(resultBuffer.hasRemaining()) {
                if (!buffer.hasRemaining()) {
                    buffer.clear();
                    if (sc.read(buffer) == -1) {
                        return resultBuffer;
                    }
                    buffer.flip();
                }
                resultBuffer.put(buffer.get());
            }
        } finally {
            buffer.compact();
        }
        return resultBuffer;
    }

    /**
     * @return a ByteBuffer in write-mode containing a content read in chunks mode
     * @throws IOException HTTPException if the connection is closed before the end
     *                     of the chunks if chunks are ill-formed
     */

    public ByteBuffer readChunks() throws IOException {
        var resultBuffer = ByteBuffer.allocate(0);
        var size = -1;
        while (size != 0) {
            var line = readLineCRLF();
            size = Integer.parseInt(line, 16);
            var bufferLine = readBytes(size);
            readBytes(2);   // "jette" \r\n

            resultBuffer.flip();
            bufferLine.flip();
            var tmpBuffer = ByteBuffer.allocate(resultBuffer.remaining() + bufferLine.remaining());
            tmpBuffer.put(resultBuffer).put(bufferLine);
            resultBuffer = tmpBuffer;
        }
        return  resultBuffer;
    }

    public static void main(String[] args) throws IOException {
        var charsetASCII = Charset.forName("ASCII");
        var request = "GET / HTTP/1.1\r\n" + "Host: www.w3.org\r\n" + "\r\n";
        var sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("www.w3.org", 80));
        sc.write(charsetASCII.encode(request));
        var buffer = ByteBuffer.allocate(50);
        var reader = new HTTPReader(sc, buffer);
        System.out.println(reader.readLineCRLF());
        System.out.println(reader.readLineCRLF());
        System.out.println(reader.readLineCRLF());
        sc.close();

        buffer = ByteBuffer.allocate(50);
        sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("www.w3.org", 80));
        reader = new HTTPReader(sc, buffer);
        sc.write(charsetASCII.encode(request));
        System.out.println(reader.readHeader());
        sc.close();


        buffer = ByteBuffer.allocate(50);
        sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("www.w3.org", 80));
        reader = new HTTPReader(sc, buffer);
        sc.write(charsetASCII.encode(request));
        var header = reader.readHeader();
        System.out.println(header);
        var content = reader.readBytes(header.getContentLength());
        content.flip();
        System.out.println(header.getCharset().orElse(Charset.forName("UTF8")).decode(content));
        sc.close();

        buffer = ByteBuffer.allocate(50);
        request = "GET / HTTP/1.1\r\n" + "Host: www.u-pem.fr\r\n" + "\r\n";
        sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("www.u-pem.fr", 80));
        reader = new HTTPReader(sc, buffer);
        sc.write(charsetASCII.encode(request));
        header = reader.readHeader();
        System.out.println(header);
        content = reader.readChunks();
        content.flip();
        System.out.println(header.getCharset().orElse(Charset.forName("UTF8")).decode(content));
        sc.close();
    }
}