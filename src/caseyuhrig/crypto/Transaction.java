package caseyuhrig.crypto;

import com.google.gson.Gson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;


/**
 * Can you transfer from multiple from addresses?
 * @author casey
 */
public class Transaction extends Record implements Comparable<Transaction>
{
    private static final Logger LOG = LogManager.getLogger(Transaction.class);
    private static final Charset charset = Charset.forName("UTF-8");
    //
    private byte[] id;
    private byte[] from; // public key
    private byte[] to; // public key
    private BigDecimal amount;
    private BigDecimal fee;
    // private byte[] creator;
    private Timestamp created;
    private byte[] signature;


    public Transaction()
    {
        setType(1000);
        setFee(new BigDecimal("0.0"));
    }


    public byte[] getID()
    {
        return id;
    }


    public void setID(final byte[] id)
    {
        this.id = id;
    }


    public byte[] getFrom()
    {
        return from;
    }


    public void setFrom(final byte[] from)
    {
        this.from = from;
    }


    public byte[] getTo()
    {
        return to;
    }


    public void setTo(final byte[] to)
    {
        this.to = to;
    }


    public BigDecimal getAmount()
    {
        return amount;
    }


    public void setAmount(final BigDecimal amount)
    {
        this.amount = amount;
    }


    public BigDecimal getFee()
    {
        return fee;
    }


    public void setFee(final BigDecimal fee)
    {
        this.fee = fee;
    }


    public Timestamp getCreated()
    {
        return created;
    }


    public void setCreated(final Timestamp created)
    {
        this.created = created;
    }


    public byte[] getSignature()
    {
        return signature;
    }


    public void setSignature(final byte[] sig)
    {
        this.signature = sig;
    }


    public Boolean isValid()
    {
        // amount > 0
        // inputs > 0
        // each input > 0
        // each output > 0
        // inputs - amount - fee = outputs
        return false;
    }


    public static Transaction deserialize(final String json)
    {
        if (Utils.isEmpty(json))
        {
            throw new EmptyParameterException("json");
        }
        final Gson gson = GsonHelper.createBuilder();
        final Transaction result = gson.fromJson(json, Transaction.class);
        return result;
    }


    public static String serialize(final Transaction tx)
    {
        final Gson gson = GsonHelper.createBuilder();
        final var json = gson.toJson(tx);
        return json.toString();
    }


    @Override
    public String toString()
    {
        return serialize(this);
    }


    public byte[] toBytes()
    {
        return toString().getBytes(charset);
    }


    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }


    /**
     * TODO Validate the transaction.
     */
    void save()
    {
        try (final var connection = new DB().getConnection())
        {
            try (final var statement = connection
                    .prepareStatement("INSERT INTO tx (from_public_key,to_public_key,amount,created,signature,txid) VALUES (?,?,?,?::TIMESTAMP,?,?)"))
            {
                statement.setBytes(1, from);
                statement.setBytes(2, to);
                statement.setBigDecimal(3, amount);
                // statement.setString(4, CryptoUtils.timestamp(created));
                statement.setTimestamp(4, created);
                statement.setBytes(5, signature);
                statement.setBytes(6, id);
                final var result = statement.executeUpdate();
                // LOG.info("Result: " + result);
                if (result != 1)
                {
                    throw new SQLException("Expecting result == 1");
                }
            }
            catch (final Throwable throwable)
            {
                throw throwable;
            }
        }
        catch (final Throwable throwable)
        {
            throw new RuntimeException(throwable.getLocalizedMessage(), throwable);
        }
    }


    /**
     * The ID is a SHA256 hash of the transaction contents.
     * @return Unique byte array for this transaction.
     */
    private byte[] createID()
    {
        // TODO NULL checks this.c = Objects.requireNonNull(c);
        final var amountBytes = String.format("%.18f", amount).getBytes(charset);
        final var feeBytes = String.format("%.18f", fee).getBytes(charset);
        final var createdBytes = CryptoUtils.timestamp(created).getBytes(charset);
        final var size = from.length + to.length + amountBytes.length + feeBytes.length + createdBytes.length;
        // create the byte buffer
        final var bytes = ByteBuffer.allocate(size);
        bytes.put(from);
        bytes.put(to);
        bytes.put(amountBytes);
        bytes.put(feeBytes);
        bytes.put(createdBytes);
        return CryptoUtils.SHA256(bytes.array());
    }


    public static Transaction create(final PaymentAddress from, final PaymentAddress to, final BigDecimal amount)
    {
        final var tx = new Transaction();
        tx.setFrom(from.getPublicKey());
        tx.setTo(to.getPublicKey());
        tx.setAmount(amount);
        tx.setCreated(new Timestamp(new Date().getTime()));
        tx.setID(tx.createID());
        tx.setSignature(from.sign(tx.getID()));
        return tx;
    }


    public Transaction submit()
    {
        save();
        return this;
    }


    public static Transaction Test()
    {
        // final var genesis = Genesis.init();
        //
        final PaymentAddress from = new PaymentAddress("Bob");
        from.save();
        final PaymentAddress to = new PaymentAddress("Alice");
        to.save();
        // post genesis transaction to Bob
        final var tx = Transaction.create(from, to, new BigDecimal("123456789012345678.000000000000000001")).submit();
        //
        // LOG.info("ID: " + Base64.getEncoder().encodeToString(tx.getID()));
        // LOG.info("FPK: " + Base64.getEncoder().encodeToString(tx.getFrom()));
        // LOG.info("TPK: " + Base64.getEncoder().encodeToString(tx.getTo()));
        // LOG.info("AMT: " + tx.getAmount().toPlainString());
        // LOG.info("SIG: " + tx.getSignature());
        // LOG.info("TS: " + tx.getCreated());
        //
        // send $ from Bob to Alice
        Send(from, to, 20.0);
        return tx;
    }


    public static void Send(final PaymentAddress from, final PaymentAddress to, final Double amount)
    {
    }


    @Override
    public int compareTo(final Transaction tx)
    {
        return fee.compareTo(tx.getFee());
    }
}
