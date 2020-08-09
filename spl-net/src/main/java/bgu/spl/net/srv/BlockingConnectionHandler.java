package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.api.StompMessagingProtocol;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final StompMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, StompMessagingProtocol<T> protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
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
                    System.out.println("message is before processing in blocking connection handler");
                    protocol.process(nextMessage);
                    System.out.println("message is after processing in blocking connection handler");
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            try{
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void close() throws IOException {
        connected = false;
        sock.close();
    }


    public void send(T msg) {
        try {
            System.out.println("blocking handler is starting to send");
            out.write(encdec.encode(msg));
            out.flush();
            System.out.println("blocking handler finished sending");
            if (protocol.shouldTerminate())
                close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}