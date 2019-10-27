import java.security.CryptoPrimitive;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;

import net.i2p.crypto.eddsa.EdDSAEngine;
import net.i2p.crypto.eddsa.EdDSASecurityProvider;
import net.i2p.crypto.eddsa.spec.EdDSANamedCurveTable;
import net.i2p.crypto.eddsa.spec.EdDSAParameterSpec;

public class ED25519{
	

    final KeyPairGenerator gen;
    
    public ED25519() throws NoSuchAlgorithmException, NoSuchProviderException {
    	Security.addProvider(new EdDSASecurityProvider());
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        gen = KeyPairGenerator.getInstance("EdDSA", "EdDSA");
        sr.setSeed(System.currentTimeMillis()); 
        gen.initialize(256, sr);
        //gen.initialize(128, sr);
    }
	public KeyPair genKeys() throws NoSuchProviderException, NoSuchAlgorithmException {;
		return gen.generateKeyPair();
	}
	
	public static byte[] sign(KeyPair kp, byte[] msg) throws SignatureException, NoSuchAlgorithmException, InvalidKeyException {
		Signature sig = Signature.getInstance("ED_25519");
		sig.initSign(kp.getPrivate());
		sig.update(msg);
		byte[] s = sig.sign();
		return s;
	}
	
	
	public static byte[] signv2(KeyPair kp, byte[] msg) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
	    EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
	    //Signature sgr = Signature.getInstance("EdDSA", "I2P");
	    Signature sgr = new EdDSAEngine(MessageDigest.getInstance(spec.getHashAlgorithm()));
	
	
	    sgr.initSign(kp.getPrivate());
	
	    sgr.update(msg);
	    return sgr.sign();
	}
	
	public static boolean verify(KeyPair kp, byte [] msg, byte [] sig) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchProviderException {
		
		EdDSAParameterSpec spec = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.ED_25519);
        Signature sgr = new EdDSAEngine(MessageDigest.getInstance(spec.getHashAlgorithm()));
  
        sgr.initVerify(kp.getPublic());

        sgr.update(msg);
        return sgr.verify(sig);
	}
}
