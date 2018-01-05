package bgu.spl181.net.impl.BBreactor;

import bgu.spl181.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl181.net.api.bidi.MessageEncoderDecoderImpl;
import bgu.spl181.net.srv.Server;

public class ReactorMain {

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);

        Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                port, //port
                BidiMessagingProtocolImpl::new, //protocol factory
                MessageEncoderDecoderImpl::new //message encoder decoder factory
        ).serve();
    }


}
