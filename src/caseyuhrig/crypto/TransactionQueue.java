package caseyuhrig.crypto;

import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;


/**
 * Transaction queue that sorts transactions by their fee, largest first.
 * TODO Add limits/overflow handling, etc.
 * TODO This whole list should probably be stored in the DB (tx_pending). Gets around any
 * concurrency issues.
 * @author casey
 */
public class TransactionQueue extends TreeSet<Transaction>
{
    private TransactionQueue()
    {
        super();
    }


    public static TransactionQueue create()
    {
        // return Collections.synchronizedSortedSet(new TransactionQueue());
        // return Collections.synchronizedNavigableMap(new TransactionQueue());
        return new TransactionQueue();
    }


    /**
     * TODO Why can't I somehow cast this... need to create a custom synchronized Queue? class.
     * @return
     */
    public static SortedSet<Transaction> createSynchronized()
    {
        return Collections.synchronizedSortedSet(new TransactionQueue());
        // return Collections.synchronizedNavigableMap(new TransactionQueue());
        // return new TransactionQueue();
    }


    @Override
    public Comparator<Transaction> comparator()
    {
        return (t1, t2) -> t1.getFee().compareTo(t2.getFee());
    }
}
