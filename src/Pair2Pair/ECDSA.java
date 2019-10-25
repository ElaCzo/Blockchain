package Pair2Pair;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class ECDSA{
	private final KeyPairGenerator keygen ;
    /**
     * @param args the command line arguments
     * @throws InvalidAlgorithmParameterException 
     * @throws NoSuchAlgorithmException 
     */
	public ECDSA() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException  {
		keygen = KeyPairGenerator.getInstance("EC");
		keygen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom());
	}
	
	public  KeyPair generateKeyPair() {
		return keygen.generateKeyPair();
	}
    
    public  byte[] sign(PrivateKey privateKey, String message) throws Exception {
        Signature signature = Signature.getInstance("SHA256withECDSA");
        signature.initSign(privateKey);

        signature.update(message.getBytes("UTF-8"));

        return signature.sign();
    }
    
    public boolean verify(PublicKey publicKey, byte[] signed, String message) throws Exception {
        Signature signature = Signature.getInstance("SHA1withECDSA");
        signature.initVerify(publicKey);
        signature.update(message.getBytes());
        return signature.verify(signed);
    }
}