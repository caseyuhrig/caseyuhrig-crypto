package caseyuhrig.crypto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;


public class Genesis
{
    private static final Logger LOG = LogManager.getLogger(Genesis.class);


    public static PaymentAddress init()
    {
        final PaymentAddress genesis = new PaymentAddress("Bob");
        genesis.save();
        //
        final var tx = Transaction.create(genesis, genesis, new BigDecimal("1000000000000000")).submit();
        // LOG.info("GAMT: " + String.format("%.18f", tx.getAmount()));
        LOG.info("Genesis INIT complete.");
        return genesis;
    }
}
