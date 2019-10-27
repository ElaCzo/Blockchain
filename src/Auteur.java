import java.io.IOException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import net.i2p.crypto.eddsa.EdDSAPublicKey;

public class Auteur extends Client{

    private List<String> letter_pool;
    private KeyPair _key;
    private String publicKeyHexa;
	
    public Auteur(String serverHost, int port) throws JSONException, UnknownHostException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
		super(serverHost, port);

        ED25519 ed = new ED25519();
        _key  = ed.genKeys();
        publicKeyHexa = Util.bytesToHex(((EdDSAPublicKey) _key.getPublic()).getAbyte());
        register();

	}
    
    public void register() throws JSONException {
		JSONObject reg = new JSONObject();
	    reg.put("register", publicKeyHexa);
        Util.writeMsg(os, reg);
        String msgread = Util.readMsg(is);
        letter_pool = Util.JSONArrayToList(Util.parseJSONObjectFromString(msgread).get("letters_bag"));
  
    }

    
    public void listen() throws JSONException {
        JSONObject listen = new JSONObject();
        listen.put("listen", JSONObject.NULL);
        Util.writeMsg(os, listen);
        
    }
    
    public void injectLetter(String c) throws JSONException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, IOException {
    	JSONObject letter = new JSONObject();
    	letter.put("letter", c);
    	letter.put("period", 0);      
        
    	letter.put("head", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
    	
    	letter.put("author", publicKeyHexa);
    	letter.put("signature", Util.bytesToHex(Sha.signLetter(_key, c, 0, Sha.hash_sha256(""))));
    	//letter.put("signature", "8b6547447108e11c0092c95e460d70f367bc137d5f89c626642e1e5f2ceb6108043d4a080223b467bb810c52b5975960eea96a2203a877f32bbd6c4dac16ec07");
    	JSONObject inject_letter = new JSONObject();
    	inject_letter.put("inject_letter", letter);
    	Util.writeMsg(os, inject_letter);

    }
    
    
    public static void main(String[] args) throws UnknownHostException, JSONException, IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {

        if(args.length!=2) {
            System.out.println("usage : command serveur port");
            System.exit(-1);
        }
        // Server Host
        final String serverHost = args[0];
        final int port = Integer.valueOf(args[1]);
        Auteur a = new Auteur(serverHost, port);
        
        
        a.listen();
        a.injectLetter(a.letter_pool.remove(0));
        while(true) {
        	
        }

    }
}
