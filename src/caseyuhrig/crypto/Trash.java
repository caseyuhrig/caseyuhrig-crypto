package caseyuhrig.crypto;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


/**
 * Bits of code I don't want to delete yet.
 * @author casey
 */
public class Trash
{
    private void oldMainStuff()
            throws Exception
    {
        // generateKeys();
        final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        final KeyPair kp = kpg.generateKeyPair();
        //
        final var publicKey = kp.getPublic();
        final var publicKeyStr = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        //
        final var privateKey = kp.getPrivate();
        final var privateKeyStr = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        //
        System.out.println(String.format("Public Key: %s", publicKeyStr));
        System.out.println(String.format("Private Key: %s", privateKeyStr));
        //
        final String input = "sample input";
        // Not a real private key! Replace with your private key!
        // final String strPk = "-----BEGIN PRIVATE KEY-----\nMIIEvwIBADANBgkqhkiG9"
        // + "w0BAQEFAASCBKkwggSlAgEAAoIBAQDJUGqaRB11KjxQ\nKHDeG"
        // + "........................................................"
        // + "Ldt0hAPNl4QKYWCfJm\nNf7Afqaa/RZq0+y/36v83NGENQ==\n" + "-----END PRIVATE
        // KEY-----\n";
        final String base64Signature2 = CryptoUtils.signSHA256RSA(privateKey.getEncoded(), input);
        System.out.println("SIGNature: " + base64Signature2);
        //
        CryptoUtils.createSignature(privateKey, input);
        final String base64Signature = CryptoUtils.signSHA256RSA(input, privateKeyStr);
        System.out.println("Signature: " + base64Signature);
        final var isGoodSig = CryptoUtils.verifySignature(publicKey, input, base64Signature);
        System.out.println("Good: " + isGoodSig);
        //
        final var wallet = new PaymentAddress("Joe");
        final var isValid = wallet.verifySignature(publicKey.getEncoded(), input, base64Signature, Charset.forName("ASCII"));
        System.out.println("Verify2: " + isValid);
    }


    private static PrivateKey loadPrivateKey(final String keyFile)
            throws IOException,
                NoSuchAlgorithmException,
                InvalidKeySpecException
    {
        /* Read all bytes from the private key file */
        final var path = Paths.get(keyFile);
        final byte[] bytes = Files.readAllBytes(path);
        /* Generate private key. */
        final var ks = new PKCS8EncodedKeySpec(bytes);
        final var kf = KeyFactory.getInstance("RSA");
        final var pvt = kf.generatePrivate(ks);
        return pvt;
    }


    private static PublicKey loadPublicKey(final String keyFile)
            throws IOException,
                NoSuchAlgorithmException,
                InvalidKeySpecException
    {
        /* Read all the public key bytes */
        final var path = Paths.get(keyFile);
        final byte[] bytes = Files.readAllBytes(path);
        /* Generate public key. */
        final X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        final KeyFactory kf = KeyFactory.getInstance("RSA");
        final PublicKey pub = kf.generatePublic(ks);
        return pub;
    }


    private static void savePrivateKey(final String outFile)
    {
        /*
         * final Base64.Encoder encoder = Base64.getEncoder();
         * // String outFile = ...;
         * final Writer out = new FileWriter(outFile + ".key");
         * out.write("-----BEGIN RSA PRIVATE KEY-----\n");
         * out.write(encoder.encodeToString(pvt.getEncoded()));
         * out.write("\n-----END RSA PRIVATE KEY-----\n");
         * out.close();
         */
    }


    private static void savePublicKey(final String outFile)
    {
        /*
         * final Base64.Encoder encoder = Base64.getEncoder();
         * final var out = new FileWriter(outFile + ".pub");
         * out.write("-----BEGIN RSA PUBLIC KEY-----\n");
         * out.write(encoder.encodeToString(kp.getPublic()));
         * out.write("\n-----END RSA PUBLIC KEY-----\n");
         * out.close();
         */
    }


    private static void generateKeys()
            throws NoSuchAlgorithmException,
                IOException
    {
        final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        final KeyPair kp = kpg.generateKeyPair();
        //
        final Key pub = kp.getPublic();
        final Key pvt = kp.getPrivate();
        //
        final String outFile = "C:\\tmp\\test-crypto";
        var out = new FileOutputStream(outFile + ".secret");
        out.write(pvt.getEncoded());
        out.close();
        //
        out = new FileOutputStream(outFile + ".public");
        out.write(pub.getEncoded());
        out.close();
        //
        System.err.println("Private key format: " + pvt.getFormat());
        // prints "Private key format: PKCS#8" on my machine
        System.err.println("Public key format: " + pub.getFormat());
        // prints "Public key format: X.509" on my machine
    }
}
