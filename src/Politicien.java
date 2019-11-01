import net.i2p.crypto.eddsa.EdDSAPublicKey;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Politicien extends Client {
    private List<Lettre> letter_pool = new ArrayList<>();
    private TreeSet<Block> blockchain;
    private List<String> word_pool;
    private KeyPair _key;

    public Politicien(String serverHost, int port) throws JSONException, UnknownHostException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        super(serverHost, port);

        ED25519 ed = new ED25519();
        _key  = ed.genKeys();
        blockchain = new TreeSet<Block>(new ComparatorBlock());
        blockchain.add(new Block(new MotVide(), null));
     }
    
    public void addToPool(Lettre l) {
    	lockletterpool.lock();
    	try {
	    	for(Lettre l2: letter_pool) {
	    		if(l2.hasSamePeriodAndAuthor(l)){
	    			return;
	    		}
	    	}
	    	//more convenient representation when computing words
	    	head_mapped.putIfAbsent(l.headHex(), new ArrayList<Lettre>());
	    	head_mapped.get(l.headHex()).add(l);
	    	letter_pool.add(l);
    	}
    	finally {
    		lockletterpool.unlock();
    	}
    	
    }

    @Override
    protected boolean traitementMessage(String msg) throws JSONException, NoSuchAlgorithmException, IOException {
        if (super.traitementMessage(msg))
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
        else if(Messages.isDiffWordPool(msg)){
            for(String w : Messages.diffWordPool(msg))
                if(!word_pool.contains(w))
                    word_pool.add(w);
            return true;
        }
        else if(Messages.isInjectLetter(msg)) {
        	addToPool(Messages.letter(msg));
        	return true;
        }
        else if(Messages.isInjectWord(msg)){
        	Mot m = Messages.word(msg);
        	if(m.isValid()) {
        		lockBlockChain.lock();
        		Block b = new Block(m, Block.getPred(m, blockchain));
        		//insert in blockchain sorted by score
        		blockchain.add(b);
        		lockBlockChain.unlock();
        	}
        	return true;
        }
        else if(Messages.isFullLetterPool(msg)) {
        	lockletterpool.lock();
        	for(Lettre l: Messages.fullLetterPool(msg)) {
        		letter_pool.add(l);
        	}
        	UtilSynchro.notifyCond(fullLetterPool, fullLetterPoolCond, this::setFullLetterPoolAvailable);
        	lockletterpool.unlock();
        	return true;
        	
        }
        return false;
    }

    public void injectWord(List<Lettre> lettres) throws JSONException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, IOException {
        byte[] public_key = ((EdDSAPublicKey)_key.getPublic()).getAbyte();
        byte[] head = lettres.get(0).getHead();
        byte[] sig = ED25519.sign(_key, Sha.hashWord(public_key, lettres, head));
        Mot m = new Mot(lettres, head, public_key, sig);
        JSONObject word = m.toJSON();
        JSONObject inject_word = new JSONObject();
        inject_word.put("inject_word", word);
        Util.writeMsg(os, inject_word);
    }
    
    
    private Map<String,List<Lettre>> head_mapped = new HashMap<String, List<Lettre>>();
    public void head_map() {
    	lockletterpool.lock();
    	head_mapped = letter_pool.stream().collect(Collectors.groupingBy(Lettre::headHex, Collectors.mapping(Function.identity(), Collectors.toList())));
    	lockletterpool.unlock();
    }
	 
    
    public List<Lettre> buildWord() throws NoSuchAlgorithmException, IOException {
    	try {
	    	//pick size of word we want to generate
	    	int max = DicoServer.MAX_SIZE;
	    	int min = DicoServer.MIN_SIZE;
	    	int size_w = (int) (Math.random() * (max - min) + min);
	    	System.out.println("trying to generate a word of size " + size_w);
	    	lockletterpool.lock();
	    	lockBlockChain.lock();
	    	
	    	System.out.println("la map est " + head_mapped);
	    	//identify most interesting blockchain for this size of word
	    	List<Lettre> letters = null;
	    	boolean found_one = false;
	    	for (Iterator<Block> i = blockchain.descendingIterator(); i.hasNext(); ) {
	    		Block b = i.next();
	    		letters = head_mapped.get(Util.bytesToHex(b.getMot().hash()));
	    		if(letters.size() >= size_w) {
	    			found_one = true;
	    			break;
	    		}
	    	}
	    	
	    	//not enough letter for this size
	    	if(!found_one) return new ArrayList<Lettre>();
	    	
	    	

	    	return letters.subList(0, size_w);
	
    	}finally {
        	lockletterpool.unlock();
	    	lockBlockChain.unlock();
    	}
    }
    
    private ReentrantLock lockletterpool = new ReentrantLock();
    private ReentrantLock lockBlockChain = new ReentrantLock();
    
    private boolean isValidWord(String w) throws IOException {
    	osDict.writeUTF(w);
    	return isDict.readBoolean();
    }
    
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
        
        p.getFullWordPool();
        try {
			UtilSynchro.waitForCond(p.fullWordPool, p.fullWordPoolCond, p::isFullWordPoolAvailable, p::setFullWordPoolAvailable);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
        
        p.getFullLetterPool();
        try {
			UtilSynchro.waitForCond(p.fullLetterPool, p.fullLetterPoolCond, p::isFullLetterPoolAvailable, p::setFullLetterPoolAvailable);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		};

        
        while(true) {
        	
        	try {
				UtilSynchro.waitForCond(p.lockNextPeriod, p.isNextPeriodCondition, p::isNextPeriod, p::setNextPeriod);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        	
        	List<Lettre> word;
        	String wordRepr;
        	do {
        		word = p.buildWord();
        		wordRepr = word.stream().map(Lettre::getL).collect(Collectors.joining());
        	}while(!p.isValidWord(wordRepr));
        	p.injectWord(word);
        	

        }
    }
}
