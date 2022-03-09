package caseyuhrig.crypto;

import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * <h1>Programmable Money</h1>
 * <p>
 * The file.encoding property has to be specified as the JVM starts up; by the time your main method
 * is entered, the character encoding used by String.getBytes() and the default constructors of
 * InputStreamReader and OutputStreamWriter has been permanently cached.
 * </p>
 * <h3>Misc. Resources</h3>
 * <ul>
 * <li>https://en.wikipedia.org/wiki/Blockchain</li>
 * <li>https://en.bitcoin.it/wiki/Protocol_rules</li>
 * <li>https://www.quickprogrammingtips.com/java/how-to-create-sha256-rsa-signature-using-java.html</li>
 * <li>https://www.novixys.com/blog/how-to-generate-rsa-keys-java/</li>
 * <li>https://github.com/jaysridhar/java-stuff/blob/master/source/java-keygen/src/main/java/sample/sample4.java</li>
 * </ul>
 * <h3>Verifiable Delay Functions</h3>
 * <ul>
 * <li>https://eprint.iacr.org/2018/601.pdf</li>
 * <li>https://www.youtube.com/watch?v=_-feyaZZjEw</li>
 * <li>https://github.com/solana-labs/solana/blob/master/poh/src/poh_service.rs</li>
 * </ul>
 * <h3>Networking</h3>
 * <ul>
 * <li>https://github.com/TooTallNate/Java-WebSocket</l1>
 * <li>Proto Buffers</i>
 * <li>https://kafka.apache.org/intro</li>
 * <li>https://www.rabbitmq.com/</li>
 * </ul>
 * <h3>Elliptic curve with Digital Signature Algorithm (ECDSA)</h3>
 * <ul>
 * <li>https://metamug.com/article/security/sign-verify-digital-signature-ecdsa-java.html</li>
 * </ul>
 * <h3>DB Fiddle</h3>
 * <ul>
 * <li>https://dbfiddle.uk/</li>
 * </ul>
 * @author <a href="mailto:casey@caseyuhrig.com">Casey Uhrig</a>
 */
public class Main
{
    private static final Logger LOG = Utils.initLogger();


    public static void main(final String... args)
    {
        LOG.info("Running main...");
        try
        {
            final var tx = Transaction.Test();
            System.out.println(tx);
            testCluster();
            LOG.info("Done");
        }
        catch (final Throwable t)
        {
            LOG.error(t.getLocalizedMessage(), t);
        }
    }


    public static void testCluster()
            throws Exception
    {
        final ArrayList<CryptoNode> nodes = new ArrayList<>();
        final String address = "localhost";
        final int port = 12000;
        final int maxNodes = 10;
        LOG.info("PORT: " + port);
        for (int n = 0; n < maxNodes; n++)
        {
            final var node = new CryptoNode("Node_" + n, address, 12000 + n);
            node.setTimeout(30);
            node.setTimeUnit(TimeUnit.SECONDS);
            node.start();
            nodes.add(node);
        }
        for (int n = 0; n < maxNodes - 1; n++)
        {
            final var node0 = nodes.get(n);
            final var node1 = nodes.get(n + 1);
            node0.connect(address, node1.getPort());
        }
        final var tx = Transaction.Test();
        final var tx2 = Transaction.Test();
        //
        nodes.get(0).broadcastAll(tx.toString());
        nodes.get(5).broadcastAll(tx2.toString());
        // CREATE statistical collector thread
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.schedule(() -> {
            try
            {
                LOG.info("shutdown");
                for (final var n : nodes)
                {
                    n.stop(1000);
                }
                LOG.info("done shutdown");
            }
            catch (final InterruptedException e)
            {
                LOG.error(e.getLocalizedMessage(), e);
            }
            executor.shutdown();
            LOG.info("Executor shutdown!");
        }, 60, TimeUnit.SECONDS);
        executor.scheduleWithFixedDelay(() -> {
            nodes.get(1).broadcastAll("Node_1 says 5 second delays rock! " + System.currentTimeMillis());
            final var tx3 = Transaction.Test();
            nodes.get(1).broadcastAll(tx3.toString());
        }, 1, 5, TimeUnit.SECONDS);
    }
}
