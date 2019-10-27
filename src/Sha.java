import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

import net.i2p.crypto.eddsa.EdDSAPublicKey;

public class Sha {
    public static byte[] hash_sha256(String text) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
        return hash;
    }
    
    public static byte[] hash_sha256(byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(bytes);
        return hash;
    }
    
    public static byte[] signLetter(KeyPair kp, String l, long period, byte[] hash_head) throws IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException {
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

    	outputStream.write( l.getBytes() );
    	outputStream.write(Util.longToBytes(period));
    	outputStream.write(hash_head);
    	
    	EdDSAPublicKey public_k= (EdDSAPublicKey) kp.getPublic();
    	outputStream.write(public_k.getAbyte());
    	byte[] hashed = hash_sha256(outputStream.toByteArray());
    	
    	byte[] signature = ED25519.signv2(kp, hashed);
    	return signature;
    }
    
    public static boolean verify(KeyPair kp, String l, long period, byte[] hash_head, byte[] sig) throws IOException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException {
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );

    	outputStream.write( l.getBytes() );
    	outputStream.write(Util.longToBytes(period));
    	outputStream.write(hash_head);
    	
    	EdDSAPublicKey public_k= (EdDSAPublicKey) kp.getPublic();
    	outputStream.write(public_k.getAbyte());
    	byte[] hashed = hash_sha256(outputStream.toByteArray());
    	
    	
    	return ED25519.verify(kp, hashed, sig);
    }
    
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, NoSuchProviderException, InvalidKeyException, SignatureException {
    	String l = new String("a");
    	long period = 0;
    	byte[] hash_head = hash_sha256("");
    	
        ED25519 ed = new ED25519();
        KeyPair kp = ed.genKeys();
        byte[] sig = signLetter(kp, l, period, hash_head);
        System.out.println(Util.bytesToHex(sig));
        
        System.out.println(verify(kp, l, period, hash_head, sig));
        
    }
}
