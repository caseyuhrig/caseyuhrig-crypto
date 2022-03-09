package caseyuhrig.crypto;

import org.java_websocket.client.WebSocketClient;

import java.net.URI;


public abstract class AbstractWebSocketClient extends WebSocketClient
{
    // private static final Logger LOG = LogManager.getLogger(CryptoWebSocketClient.class);
    AbstractWebSocketClient(final String hostname, final int port)
    {
        // super(uri, new Draft_6455());
        super(URI.create(hostname + ":" + port));
        // final var remoteAddress = this.getRemoteSocketAddress();
    }


    AbstractWebSocketClient(final URI uri)
    {
        super(uri);
    }
}
