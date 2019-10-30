import net.i2p.crypto.eddsa.EdDSAPublicKey;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Politicien extends Client {
    Block block;
    private List<Lettre> letter_pool = new ArrayList<>();
    private List<String> word_pool;
    private KeyPair _key;

    public Politicien(String serverHost, int port) throws JSONException, UnknownHostException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        super(serverHost, port);

        ED25519 ed = new ED25519();
        _key  = ed.genKeys();
     }
    
    public void addToPool(Lettre l) {
    	lockletterpool.lock();
    	try {
	    	for(Lettre l2: letter_pool) {
	    		if(l2.hasSamePeriodAndAuthor(l)){
	    			return;
	    		}
	    	}
	    	letter_pool.add(l);
    	}
    	finally {
    		lockletterpool.unlock();
    	}
    	
    }

    @Override
    protected boolean traitementMessage(String msg) throws JSONException {
        if (super.traitementMessage(msg))
            return true;
        else if(Messages.isFullWordPool(msg)){
            word_pool=Messages.fullWordPool(msg);
            return true;
        }
        else if(Messages.isDiffWordPool(msg)){
            for(String w : Messages.diffWordPool(msg))
                if(!word_pool.contains(w))
                    word_pool.add(w);
            return true;
        }
        else if(Messages.isInjectLetter(msg)) {
        	addToPool(Messages.letter(msg));
        }
        else if(Messages.isFullLetterPool(msg)) {
        	lockletterpool.lock();
        	for(Lettre l: Messages.fullLetterPool(msg)) {
        		letter_pool.add(l);
        	}
        	UtilSynchro.notifyCond(fullLetterPool, fullLetterPoolCond, this::setFullLetterPoolAvailable);
        	lockletterpool.unlock();
        	
        }
        return false;
    }

    public void injectWord(List<Lettre> lettres) throws JSONException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, IOException {
        byte[] public_key = ((EdDSAPublicKey)_key.getPublic()).getAbyte();
        byte[] sig = ED25519.sign(_key, Sha.hashWord(public_key, lettres, head));
        Mot m = new Mot(lettres, head, public_key, sig);
        JSONObject word = m.toJSON();
        JSONObject inject_word = new JSONObject();
        inject_word.put("inject_word", word);
        Util.writeMsg(os, inject_word);
    }
    
    
    
    public List<Lettre> buildWord() {
    	List<Lettre> res = new ArrayList<Lettre>();
    	lockletterpool.lock();
    	for(Lettre l:letter_pool) {
    		res.add(l);
    	}
    	lockletterpool.unlock();
    	return res;
    }
    
    private ReentrantLock lockletterpool = new ReentrantLock();
    

    
    public static void main(String[] args) throws UnknownHostException, JSONException, IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {

        if(args.length!=2) {
            System.out.println("usage : command serveur port");
            System.exit(-1);
        }
        // Server Host
        final String serverHost = args[0];
        final int port = Integer.valueOf(args[1]);
        Politicien p = new Politicien(serverHost, port);
        p.listen();
        p.getFullLetterPool();
        try {
			UtilSynchro.waitForCond(p.fullLetterPool, p.fullLetterPoolCond, p::isFullLetterPoolAvailable, p::setFullLetterPoolAvailable);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		};

        
        while(true) {
        	p.injectWord(p.buildWord());
        	try {
				UtilSynchro.waitForCond(p.lockNextPeriod, p.isNextPeriodCondition, p::isNextPeriod, p::setNextPeriod);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
    }
}
