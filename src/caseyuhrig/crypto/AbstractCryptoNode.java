package caseyuhrig.crypto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * AbstractCryptoWebSocketNode wires up a WebSocket Server and an array of WebSocket Client's which
 * can allow clients to connect into this node and this node to connect to other nodes/servers. Act
 * as a client and server.
 * @author casey
 */
public abstract class AbstractCryptoNode extends AbstractWebSocketServer
{
    private static final Logger LOG = LogManager.getLogger(AbstractCryptoNode.class);
    private final String name;
    private final ArrayList<WebSocketClient> clients;
    private final ScheduledThreadPoolExecutor executor;
    private long timeout = 0;
    private TimeUnit timeUnit = TimeUnit.SECONDS;


    public AbstractCryptoNode(final String name, final String hostname, final int port)
            throws UnknownHostException
    {
        super(new InetSocketAddress(hostname, port));
        // Draft_6455 draft
        // super(new InetSocketAddress(port), Collections.<Draft> singletonList(draft));
        this.name = name;
        clients = new ArrayList<>();
        executor = new ScheduledThreadPoolExecutor(1);
    }


    public abstract void handleStart();
    /**
     * Executes before the underlying network connections and threads are closed for this node.
     * Gives a chance to do any cleanup before underlying resources are freed.
     */
    public abstract void handleStop();
    public abstract void handleOpen(final WebSocket connection, final Handshakedata handshake);
    public abstract void handleClose(final WebSocket connection, final int code, final String reason, final boolean remote);
    public abstract void handleMessage(final WebSocket connection, final String message);
    public abstract void handleError(final WebSocket connection, final Exception exception);


    public String getName()
    {
        return name;
    }


    public long getTimeout()
    {
        return this.timeout;
    }


    public void setTimeout(final long timeout)
    {
        this.timeout = timeout;
    }


    public TimeUnit getTimeUnit()
    {
        return this.timeUnit;
    }


    public void setTimeUnit(final TimeUnit timeUnit)
    {
        this.timeUnit = timeUnit;
    }


    public void connect(final String hostname, final int port)
    {
        // client constructor super(uri, new Draft_6455());
        final var client = new AbstractWebSocketClient(URI.create("ws://" + hostname + ":" + port))
        {
            @Override
            public void onOpen(final ServerHandshake handshake)
            {
                AbstractCryptoNode.this.openHandler(getConnection(), handshake);
            }


            @Override
            public void onMessage(final String message)
            {
                AbstractCryptoNode.this.messageHandler(getConnection(), message);
            }


            @Override
            public void onClose(final int code, final String reason, final boolean remote)
            {
                AbstractCryptoNode.this.closeHandler(getConnection(), code, reason, remote);
            }


            @Override
            public void onError(final Exception exception)
            {
                AbstractCryptoNode.this.errorHandler(getConnection(), exception);
            }
        };
        try
        {
            if (timeout > 0)
            {
                final var host = hostname + ":" + port;
                final long startTime = System.currentTimeMillis();
                LOG.info("Connecting to (blocking): " + host + "...");
                client.connectBlocking(timeout, timeUnit);
                final long duration = System.currentTimeMillis() - startTime;
                LOG.info("Connected to: " + host + " " + duration + "ms");
            }
            else
            {
                client.connect();
            }
        }
        catch (final InterruptedException e)
        {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
        clients.add(client);
    }


    /**
     * Broadcasts to all nodes connected into this node and all nodes this node is connected into.
     */
    public void broadcastAll(final String message)
    {
        // final Collection<WebSocket> connections = null;
        // final CryptoWebSocketClient client = clients.get(0);
        // final WebSocket ws = client.getConnection();
        //
        //
        // super.broadcast(message);
        final var connections = getConnections();
        for (final var connection : connections)
        {
            try
            {
                connection.send(message);
                // broadcast(message, connection);
            }
            catch (final Throwable throwable)
            {
                LOG.error(throwable.getLocalizedMessage(), throwable);
            }
        }
        for (final var client : clients)
        {
            try
            {
                client.send(message);
            }
            catch (final Throwable throwable)
            {
                LOG.error(throwable.getLocalizedMessage(), throwable);
            }
        }
        // not sure if that will work, we need to act as a client when sending to the servers
        // we are connected into as a client
        // final List<WebSocket> connections = clients.stream().map(c ->
        // c.getConnection()).collect(Collectors.toList());
        // super.broadcast(message, connections);
    }


    @Override
    public void onStart()
    {
        startHandler();
    }


    @Override
    public void onOpen(final WebSocket connection, final ClientHandshake handshake)
    {
        openHandler(connection, handshake);
    }


    @Override
    public void onMessage(final WebSocket connection, final String message)
    {
        messageHandler(connection, message);
    }


    @Override
    public void onClose(final WebSocket connection, final int code, final String reason, final boolean remote)
    {
        closeHandler(connection, code, reason, remote);
    }


    @Override
    public void onError(final WebSocket connection, final Exception exception)
    {
        errorHandler(connection, exception);
    }


    public void openHandler(final WebSocket connection, final Handshakedata handshake)
    {
        handleOpen(connection, handshake);
    }


    private void messageHandler(final WebSocket connection, final String message)
    {
        // final var localHost = connection.getLocalSocketAddress().getAddress().getHostAddress();
        // final var localPort = connection.getLocalSocketAddress().getPort();
        // final var local = localHost + ":" + localPort;
        // final var remoteHost = connection.getRemoteSocketAddress().getAddress().getHostAddress();
        // final var remotePort = connection.getRemoteSocketAddress().getPort();
        // final var remote = remoteHost + ":" + remotePort;
        // LOG.info(remote + " => " + local + " msg: " + message);
        handleMessage(connection, message);
    }


    private void closeHandler(final WebSocket connection, final int code, final String reason, final boolean remote)
    {
        handleClose(connection, code, reason, remote);
    }


    private void errorHandler(final WebSocket connection, final Exception exception)
    {
        handleError(connection, exception);
    }


    public void startHandler()
    {
        handleStart();
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }


    @Override
    public void stop(final int timeout)
            throws InterruptedException
    {
        handleStop();
        executor.shutdown();
        super.stop(timeout);
    }
}
