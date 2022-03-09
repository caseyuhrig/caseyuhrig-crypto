package caseyuhrig.crypto;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class PaymentAddress
{
    // private static final Logger LOG = LogManager.getLogger(PaymentAddress.class);
    //
    private final String label;
    private final byte[] publicKey;
    private final byte[] privateKey;


    /**
     * @TODO Update the key generation to ECDSA
     * @param label
     */
    public PaymentAddress(final String label)
    {
        try
        {
            this.label = label;
            // https://metamug.com/article/security/sign-verify-digital-signature-ecdsa-java.html
            final var kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            final var kp = kpg.generateKeyPair();
            publicKey = kp.getPublic().getEncoded();
            privateKey = kp.getPrivate().getEncoded();
        }
        catch (final NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }


    public PaymentAddress(final String label, final byte[] publicKey, final byte[] privateKey)
    {
        this.label = label;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }


    public byte[] getPublicKey()
    {
        return publicKey;
    }


    /**
     * Create base64 encoded signature using SHA256/RSA.
     * @param data
     * @return
     */
    public byte[] sign(final byte[] data)
    {
        try
        {
            final var spec = new PKCS8EncodedKeySpec(privateKey);
            // final var value = spec.getAlgorithm();
            // LOG.info("Spec Alg: " + value);
            final var kf = KeyFactory.getInstance("RSA");
            final var privateSignature = Signature.getInstance("SHA256withRSA");
            privateSignature.initSign(kf.generatePrivate(spec));
            privateSignature.update(data);
            final byte[] s = privateSignature.sign();
            return s;
        }
        catch (final Throwable t)
        {
            throw new RuntimeException(t.getLocalizedMessage(), t);
        }
    }


    public boolean verifySignature(final byte[] publicKey, final byte[] data, final byte[] signedData)
            throws InvalidKeyException,
                InvalidKeySpecException,
                SignatureException,
                NoSuchAlgorithmException
    {
        // RSAPublicKeySpec(publicKey);
        // PKCS8EncodedKeySpec(publicKey);
        final var spec = new X509EncodedKeySpec(publicKey);
        // final var value = spec.getFormat();
        // LOG.info("Spec Alg2: " + value);
        final var kf = KeyFactory.getInstance("RSA");
        final var sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(kf.generatePublic(spec));
        sign.update(data);
        return sign.verify(signedData);
    }


    public boolean verifySignature(final byte[] publicKey, final String value, final String signedValue, final Charset charset)
            throws InvalidKeyException,
                InvalidKeySpecException,
                SignatureException,
                NoSuchAlgorithmException
    {
        // final var charset = Charset.forName("ASCII");
        final var bytes = value.getBytes(charset);
        final var signedBytes = Base64.getDecoder().decode(signedValue);
        return verifySignature(publicKey, bytes, signedBytes);
    }


    void save()
    {
        try (final var connection = new DB().getConnection())
        {
            try (final var statement = connection
                    .prepareStatement("INSERT INTO payment_addr (public_key,private_key,label) VALUES (?,?,?)"))
            {
                statement.setBytes(1, publicKey);
                statement.setBytes(2, privateKey);
                statement.setString(3, label);
                final var result = statement.executeUpdate();
                if (result != 1)
                {
                    throw new RuntimeException("Statement result != 1");
                }
                // LOG.info("Result: " + result);
            }
            catch (final Throwable throwable)
            {
                // LOG.error(throwable.getLocalizedMessage(), throwable);
                throw throwable;
            }
        }
        catch (final Throwable throwable)
        {
            // LOG.error(throwable.getLocalizedMessage(), throwable);
            throw new RuntimeException(throwable.getLocalizedMessage(), throwable);
        }
    }
}
