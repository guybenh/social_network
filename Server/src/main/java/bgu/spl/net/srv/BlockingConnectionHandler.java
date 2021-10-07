package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocolA;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.messages.ServerToClientMessages;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocolA<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    @SuppressWarnings("unchecked")
    public BlockingConnectionHandler(ConnectionsImpl connections, Socket sock, MessageEncoderDecoder<T> reader,
                                     BidiMessagingProtocolA<T> protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        protocol.start(connections.getConnectionID(), connections);
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());
            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {
        try {
            out.write(((ServerToClientMessages)msg).encode());
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}