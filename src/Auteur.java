import java.io.IOException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;

import net.i2p.crypto.eddsa.EdDSAPublicKey;

public class Auteur extends Client{

    private List<String> letter_pool;
    private KeyPair _key;
    private String publicKeyHexa;

    @Override
    protected boolean traitementMessage(String msg) throws JSONException {
        if(super.traitementMessage(msg))
            return true;
        else if(Messages.isFullLetterPool(msg)){
            letter_pool=Messages.fullLetterPool(msg);
            return true;
        }
        else if(Messages.isDiffLetterPool(msg)){
            for(String l : Messages.diffLetterPool(msg)) {
                if (!letter_pool.contains(l))
                    letter_pool.add(l);
            }
            return true;
        }
        else if(Messages.isLettersBag(msg)) {
            letter_pool = Messages.lettersBag(msg);
            notifyLetterPool();
            return true;
        }
        return false;
    }

    public Auteur(String serverHost, int port) throws JSONException, UnknownHostException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        super(serverHost, port);

        ED25519 ed = new ED25519();
        _key  = ed.genKeys();
        publicKeyHexa = Util.bytesToHex(((EdDSAPublicKey) _key.getPublic()).getAbyte());
        register();
        
        //for now
        period = 0;
    }

    public void register() throws JSONException {
        JSONObject reg = new JSONObject();
        reg.put("register", publicKeyHexa);
        Util.writeMsg(os, reg);
    }

    public void getFullLetterPool() throws JSONException {
        JSONObject getFullLetterPool = new JSONObject();
        getFullLetterPool.put("get_full_letterpool", JSONObject.NULL);
        Util.writeMsg(os, getFullLetterPool);
    }

    public void getLetterPoolSince(int period) throws JSONException {
        JSONObject getLetterPoolSince = new JSONObject();
        getLetterPoolSince.put("get_letterpool_since", period+"");
        Util.writeMsg(os, getLetterPoolSince);
    }

    public void getFullWordPool() throws JSONException {
        JSONObject getFullWordPool = new JSONObject();
        getFullWordPool.put("get_full_wordpool", JSONObject.NULL);
        Util.writeMsg(os, getFullWordPool);
    }

    public void getWordPoolSince(int period) throws JSONException {
        JSONObject getWordPoolSince = new JSONObject();
        getWordPoolSince.put("get_wordpool_since", period+"");
        Util.writeMsg(os, getWordPoolSince);
    }

    public void injectLetter(String c) throws JSONException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, IOException {
 
        byte[] public_key = ((EdDSAPublicKey) _key.getPublic()).getAbyte();
        byte[] sig = ED25519.sign(_key, Sha.hashLetter(public_key, c, period, head));
        
        Lettre l = new Lettre(c, period, head, public_key, sig);
        JSONObject letter = l.toJSON();
        JSONObject inject_letter = new JSONObject();
        inject_letter.put("inject_letter", letter);
        Util.writeMsg(os, inject_letter);
    }
    
    private ReentrantLock lockLetterPool = new ReentrantLock();
    private Condition letterPoolAvailableCondition = lockLetterPool.newCondition();
    private boolean letterPoolAvailable = false;
    public void waitForLetterPool() throws InterruptedException {
    	lockLetterPool.lock();
    	try {
    		while(!letterPoolAvailable)
    			letterPoolAvailableCondition.await();
    	}
    	finally {
    		lockLetterPool.unlock();
    	}
    }
    
    public void notifyLetterPool()  {
    	lockLetterPool.lock();
    	try {
    		letterPoolAvailable = true;
    		letterPoolAvailableCondition.signalAll();
    	}
    	finally {
    		lockLetterPool.unlock();
    	}
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
        try {
			a.waitForLetterPool();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        a.injectLetter(a.letter_pool.remove(0));
        while(true) {

        }
    }
}
