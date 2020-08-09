package bgu.spl.net.impl.stomp;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.StompMessagingProtocol;
import bgu.spl.net.srv.Frame;
import bgu.spl.net.srv.MessageEncoderDecoderImpl;
import bgu.spl.net.srv.Server;
import bgu.spl.net.srv.StompMessagingProtoclImpl;

import java.awt.*;










public class StompServer {

    public static void main(String[] args) {

        if (args[1].equals("tpc") ) {
            Server.threadPerClient(Integer.parseInt(args[0]), () -> new StompMessagingProtoclImpl(), () -> new MessageEncoderDecoderImpl()).serve();
        }
        if (args[1].equals("reactor") ) {
            Server.reactor(
                    Runtime.getRuntime().availableProcessors(),
                    Integer.parseInt(args[0]), //port
                    () -> new StompMessagingProtoclImpl(), //protocol factory
                    MessageEncoderDecoderImpl::new //message encoder decoder factory
            ).serve();
        }
    }
}