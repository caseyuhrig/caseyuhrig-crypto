package caseyuhrig.crypto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.Handshakedata;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.SortedSet;


public class CryptoNode extends AbstractCryptoNode
{
    private static final Logger LOG = LogManager.getLogger(CryptoNode.class);
    private final ArrayList<String> messages;
    private final SortedSet<Transaction> queue;


    public CryptoNode(final String name, final String hostname, final int port)
            throws UnknownHostException
    {
        super(name, hostname, port);
        messages = new ArrayList<>();
        queue = TransactionQueue.create();
    }


    @Override
    public void handleStart()
    {
        LOG.info("Server started! " + getPort());
    }


    @Override
    public void handleMessage(final WebSocket connection, final String message)
    {
        // Check if the message is a duplicate.
        // FIXME The duplicate list only works for the time the node has been active. The list also
        // needs to have a limit. Older items should be pushed out of the list when the limit has
        // been reached.
        if (!messages.contains(message))
        {
            LOG.info("MSG[" + getName() + "]: " + message);
            if (GsonHelper.isJSON(message))
            {
                final var tx = Transaction.deserialize(message);
                LOG.info("TX: " + tx);
                queue.add(tx);
                queue.remove(tx);
            }
            messages.add(message);
            broadcastAll(message);
        }
        else
        {
            // LOG.error("dup msg: " + message);
        }
    }


    @Override
    public void handleOpen(final WebSocket connection, final Handshakedata handshake)
    {
        // LOG.info("open");
    }


    @Override
    public void handleClose(final WebSocket connection, final int code, final String reason, final boolean remote)
    {
        // LOG.info("close");
    }


    @Override
    public void handleError(final WebSocket connection, final Exception exception)
    {
        LOG.error(exception.getLocalizedMessage(), exception);
    }


    @Override
    public void handleStop()
    {
        LOG.error("Stopping Crypto Node!");
    }
}
