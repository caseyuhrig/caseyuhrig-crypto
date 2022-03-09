package caseyuhrig.crypto;

import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collections;


public abstract class AbstractWebSocketServer extends WebSocketServer
{
    // private static final Logger LOG = LogManager.getLogger(CryptoWebSocketServer.class);
    public AbstractWebSocketServer(final String hostname, final int port)
            throws UnknownHostException
    {
        super(new InetSocketAddress(hostname, port));
    }


    public AbstractWebSocketServer(final InetSocketAddress address)
    {
        super(address);
    }


    public AbstractWebSocketServer(final int port, final Draft_6455 draft)
    {
        super(new InetSocketAddress(port), Collections.<Draft> singletonList(draft));
    }
}