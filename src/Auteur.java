import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;
import org.json.JSONObject;

import net.i2p.crypto.eddsa.EdDSAPublicKey;

public class Auteur extends Client{

    private List<String> letter_bag;
    //kind of similar to word pool but dont have to rebuild the chain all the time
    private TreeSet<Block> blockchain;
    private KeyPair _key;
    private String publicKeyHexa;
    


    @Override
    protected boolean traitementMessage(String msg) throws JSONException, NoSuchAlgorithmException, IOException {
    	if(super.traitementMessage(msg))
            return true;
        else if(Messages.isFullWordPool(msg)){
        	lockBlockChain.lock();
            List<Mot> words = Messages.fullWordPool(msg);
            for(Mot m: words) {
            	Block b = new Block(m, Block.getPred(m, blockchain));
            	blockchain.add(b);
            }
        	lockBlockChain.unlock();
            UtilSynchro.notifyCond(fullWordPool, fullWordPoolCond, this::setFullWordPoolAvailable);   	
            
            return true;
        }
        /*
        else if(Messages.isFullLetterPool(msg)){
            letter_bag=Messages.fullLetterPool(msg);
            return true;
        }
        /*
        else if(Messages.isDiffLetterPool(msg)){
            for(String l : Messages.diffLetterPool(msg)) {
                if (!letter_pool.contains(l))
                    letter_pool.add(l);
            }
            return true;
        }*/
        else if(Messages.isLettersBag(msg)) {
            letter_bag = Messages.lettersBag(msg);
            UtilSynchro.notifyCond(lockLetterPool, letterPoolAvailableCondition, this::setLetterPoolAvailable);
            //notifyLetterPool();
            return true;
        }
        else if(Messages.isInjectLetter(msg)) {
        	//do nothing
        	return true;
        }
        else if(Messages.isInjectWord(msg)) {
        	Mot m = Messages.word(msg);
        	if(m.isValid()) {
        		lockBlockChain.lock();
        		Block b = new Block(m, Block.getPred(m, blockchain));
        		
        		boolean s = blockchain.add(b);
        		/*
        		System.out.println("score is " +b.getScore());
        		System.out.println("etat de la blockchain apres injection " + s);
        		*/
        		lockBlockChain.unlock();
        	}
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
        blockchain = new TreeSet<Block>(new ComparatorBlock());
        blockchain.add(new Block(new MotVide(), null));
        
        //set period with msg full_word_pool would be better
        period = 0;
    }

    public void register() throws JSONException {
        JSONObject reg = new JSONObject();
        reg.put("register", publicKeyHexa);
        Util.writeMsg(os, reg);
    }



    public void injectLetter(String c) throws JSONException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, IOException {
        byte[] public_key = ((EdDSAPublicKey) _key.getPublic()).getAbyte();
        lockBlockChain.lock();
        lockNextPeriod.lock();
        long p = period;
        byte[] head = blockchain.last().getMot().hash();
        /*
        System.out.println("etat de la blockchain " + blockchain);
        System.out.println("mot choisi sur lequel injecter " + blockchain.last().getMot().toJSON().toString());
        */
        byte[] sig = ED25519.sign(_key, Sha.hashLetter(public_key, c, p, head));
        lockBlockChain.unlock();
        lockNextPeriod.unlock();
        Lettre l = new Lettre(c, p, head, public_key, sig);
        JSONObject letter = l.toJSON();
        JSONObject inject_letter = new JSONObject();
        inject_letter.put("inject_letter", letter);
        Util.writeMsg(os, inject_letter);
    }
    
    private ReentrantLock lockLetterPool = new ReentrantLock();
    private Condition letterPoolAvailableCondition = lockLetterPool.newCondition();
    private boolean letterPoolAvailable = false;
    
    public void setLetterPoolAvailable(boolean b) {
    	letterPoolAvailable = true;
    }   
    public boolean getLetterPoolAvailable() {
    	return letterPoolAvailable;
    }

    
    private ReentrantLock lockFullWordPool = new ReentrantLock();
    private Condition fullWordPoolAvailableCondition = lockLetterPool.newCondition();
    private boolean fullWordPoolAvailable = false;

    private ReentrantLock lockBlockChain = new ReentrantLock();
    private Condition letterblockChainAvailableCondition = lockLetterPool.newCondition();
    private boolean blockChainAvailable = false;
    
    public boolean isBlockChainAvailable() {
		return blockChainAvailable;
	}

	public void setBlockChainAvailable(boolean blockChainAvailable) {
		this.blockChainAvailable = blockChainAvailable;
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
			UtilSynchro.waitForCond(a.lockLetterPool, a.letterPoolAvailableCondition, a::getLetterPoolAvailable, a::setLetterPoolAvailable);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        while(true) {
        	a.injectLetter(a.letter_bag.remove(0));
        	try {
				UtilSynchro.waitForCond(a.lockNextPeriod, a.isNextPeriodCondition, a::isNextPeriod, a::setNextPeriod);
				Thread.sleep(10000);
        	} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	
        }
    }
}
