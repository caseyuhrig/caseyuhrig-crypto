package caseyuhrig.crypto;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Base64;


public class CryptoUtils
{
    private static final Logger LOG = LogManager.getLogger(CryptoUtils.class);


    public static byte[] SHA256(final byte[] data)
    {
        try
        {
            return MessageDigest.getInstance("SHA-256").digest(data);
        }
        catch (final NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }


    public static byte[] SHA3_256(final byte[] data)
    {
        try
        {
            return MessageDigest.getInstance("SHA3-256").digest(data);
        }
        catch (final NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e.getLocalizedMessage(), e);
        }
    }


    // Create base64 encoded signature using SHA256/RSA.
    public static String signSHA256RSA(final byte[] privateKey, final String input)
            throws Exception
    {
        // Remove markers and new line characters in private key
        // final String realPK = strPk.replaceAll("-----END PRIVATE KEY-----",
        // "").replaceAll("-----BEGIN PRIVATE KEY-----", "")
        // .replaceAll("\n", "");
        // final byte[] b1 = Base64.getDecoder().decode(realPK);
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey);
        final KeyFactory kf = KeyFactory.getInstance("RSA");
        final Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(kf.generatePrivate(spec));
        signature.update(input.getBytes("UTF-8"));
        final byte[] s = signature.sign();
        return Base64.getEncoder().encodeToString(s);
    }


    public static String createSignature(final PrivateKey pvt, final String data)
            throws NoSuchAlgorithmException,
                InvalidKeyException,
                SignatureException
    {
        final Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(pvt);
        final var charset = Charset.forName("ASCII");
        final byte[] bytes = data.getBytes(charset);
        sign.update(bytes, 0, bytes.length);
        final byte[] signature = sign.sign();
        final var sig = Base64.getEncoder().encodeToString(signature);
        return sig;
        // System.out.println(String.format("SIGNATURE: %s", sig));
        /*
         * InputStream in = null;
         * try
         * {
         * in = new FileInputStream(dataFile);
         * final byte[] buf = new byte[2048];
         * int len;
         * while ((len = in.read(buf)) != -1)
         * {
         * sign.update(buf, 0, len);
         * }
         * }
         * finally
         * {
         * if (in != null)
         * {
         * in.close();
         * }
         * }
         */
        /*
         * OutputStream out = null;
         * try
         * {
         * out = new FileOutputStream(signFile);
         * final byte[] signature = sign.sign();
         * out.write(signature);
         * }
         * finally
         * {
         * if (out != null)
         * {
         * out.close();
         * }
         * }
         */
    }


    public static Boolean verifySignature(final PublicKey publicKey, final String input, final String signInput)
            throws NoSuchAlgorithmException,
                InvalidKeyException,
                SignatureException,
                UnsupportedEncodingException
    {
        // Base64.getEncoder().encodeToString(s);
        // final var bytes = Base64.getDecoder().decode(input);
        final var charset = Charset.forName("ASCII");
        final Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(publicKey);
        //
        final var inBytes = input.getBytes(charset);
        sign.update(inBytes);
        //
        final var bytes = Base64.getDecoder().decode(signInput);
        return sign.verify(bytes);
        /*
         * // InputStream in = null;
         * try (final var in = new FileInputStream(dataFile))
         * {
         * final byte[] buf = new byte[2048];
         * int len;
         * while ((len = in.read(buf)) != -1)
         * {
         * sign.update(buf, 0, len);
         * }
         * }
         * // Read the signature bytes from file
         * final var path = Paths.get(signFile);
         * final var bytes = Files.readAllBytes(path);
         * System.out.println(dataFile + ": Signature " + (sign.verify(bytes) ? "OK" : "Not OK"));
         */
        // return false;
    }


    // Create base64 encoded signature using SHA256/RSA.
    public static String signSHA256RSA(final String input, final String strPk)
            throws Exception
    {
        // Remove markers and new line characters in private key
        final String realPK = strPk.replaceAll("-----END PRIVATE KEY-----", "").replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("\n", "");
        final byte[] b1 = Base64.getDecoder().decode(realPK);
        final PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
        final KeyFactory kf = KeyFactory.getInstance("RSA");
        final Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(kf.generatePrivate(spec));
        privateSignature.update(input.getBytes("UTF-8"));
        final byte[] s = privateSignature.sign();
        return Base64.getEncoder().encodeToString(s);
    }


    public static byte[] concat(final byte[] a, final byte[] b)
    {
        final byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }


    public static String timestamp(final Timestamp timestamp)
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(timestamp);
    }


    public static byte[] SHA_512(final byte[] data)
    {
        try
        {
            return MessageDigest.getInstance("SHA-512").digest(data);
            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            // final byte[] messageDigest = md.digest(data);
            // Convert byte array into signum representation
            // final BigInteger no = new BigInteger(1, messageDigest);
            // Convert message digest into hex value
            // String hashtext = no.toString(16);
            // Add preceding 0s to make it 32 bit
            // while (hashtext.length() < 32)
            // {
            // hashtext = "0" + hashtext;
            // }
            // return the HashText
            // return hashtext;
        }
        // For specifying wrong message digest algorithms
        catch (final NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }


    public static void pause(final Object obj, final long milliseconds)
    {
        try
        {
            synchronized (obj)
            {
                obj.wait(milliseconds);
            }
        }
        catch (final InterruptedException e)
        {
            LOG.error(e.getLocalizedMessage(), e);
        }
    }
    //
    // ECDSA Digital Signature Verification
    // https://lh3.googleusercontent.com/-AvMp1W2QAIc/Wz2rtqvybJI/AAAAAAAAEew/HYxhwdWJ5jYnKA8BX5K3eKHowJHyOBivwCL0BGAYYCw/h359/2018-07-04.png
    //
    // ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
    // KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
    // g.initialize(ecSpec, new SecureRandom());
    // KeyPair keypair = g.generateKeyPair();
    // PublicKey publicKey = keypair.getPublic();
    // PrivateKey privateKey = keypair.getPrivate();
    //
    // -- at sender's end
    // Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
    // ecdsaSign.initSign(privateKey);
    // ecdsaSign.update(plaintext.getBytes("UTF-8"));
    // byte[] signature = ecdsaSign.sign();
    // String pub = Base64.getEncoder().encodeToString(publicKey.getEncoded());
    // String sig = Base64.getEncoder().encodeToString(signature);
    // -- at receiver's end
    // Signature ecdsaVerify = Signature.getInstance(obj.getString("algorithm"));
    // KeyFactory kf = KeyFactory.getInstance("EC");
    // EncodedKeySpec publicKeySpec = new
    // X509EncodedKeySpec(Base64.getDecoder().decode(obj.getString("publicKey")));
    // KeyFactory keyFactory = KeyFactory.getInstance("EC");
    // PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
    // ecdsaVerify.initVerify(publicKey);
    // ecdsaVerify.update(obj.getString("message").getBytes("UTF-8"));
    // boolean result = ecdsaVerify.verify(Base64.getDecoder().decode(obj.getString("signature")));
}
