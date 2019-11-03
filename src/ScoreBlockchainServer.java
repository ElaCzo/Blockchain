import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.json.JSONException;

public class ScoreBlockchainServer {

    static final int PORT = 1988;
    static final int MIN_SIZE = 1;
    static final int MAX_SIZE = 1;
    
    static private ReentrantLock lockauthor = new ReentrantLock();
    static private ReentrantLock lockpoli = new ReentrantLock();
    static private int nb_author = 0;
    static private int nb_poli = 0;
    static private boolean kill_poli = false;
    
    
    static private List<DataOutputStream> pol = new ArrayList<>();
    static private ReentrantLock locklistpoli = new ReentrantLock();
    static private Politicien p;
    static private Map<String, Integer> scoreAuthor = new HashMap<String, Integer>();
    static private Map<String, Integer> scorePoli = new HashMap<String, Integer>();
    
    static public void registerPoli(DataOutputStream os) {
    	locklistpoli.lock();
    	pol.add(os);
    	locklistpoli.unlock();
    }
    
    static public void freePoli() throws IOException {
    	locklistpoli.lock();
    	for(DataOutputStream os: pol) {
    		os.writeBoolean(true);
    	}
    	locklistpoli.unlock();
    }
    
    static public void incNbAuthor() {
    	lockauthor.lock();
    	nb_author++;
    	lockauthor.unlock();
    }
    
    static public void decNbAuthor() throws IOException {
    	lockauthor.lock();
    	nb_author--;
    	if(nb_author == 0) {
    		kill_poli = true;
    		freePoli();
    	}
    	lockauthor.unlock();
    }
    
    static public void incNbPoli() {
    	lockpoli.lock();
    	nb_poli++;
    	lockpoli.unlock();
    }
    
    static int decNbPoli() {
    	try {
	    	lockpoli.lock();
	    	System.out.println("nb poli vaut " + nb_poli);
	    	nb_poli--;
	    	if(nb_poli == 0) {
	    		computeScoreBlockchain();
	    	}
	    	return nb_poli;
    	}finally {
    		lockpoli.unlock();
    	}
    }
    
    static public boolean continueOrNot() {
    	try {
    		lockauthor.lock();
        	return kill_poli;
    	}finally {
    		lockauthor.unlock();
    	}
    }
    
   static public void computeScoreBlockchain() {
	   Block b = p.getBlockchain().last();
	   while (b.getPred() != null){
		   System.out.println("bloc choisi " + b);
		   String pId = b.getMot().politicianId();
		   int scoreMot = b.getMot().getScore();
		   if(!scorePoli.containsKey(pId)) {
			   scorePoli.put(pId, 0);
		   }
		   int prv_score = scorePoli.get(pId);
		   scorePoli.put(pId,scoreMot+prv_score);
		   
		   for(Lettre l: b.getMot().getLetters()) {
			   String aId = l.authorId();
			   int scoreLettre = l.scrableValue();
			   if(!scoreAuthor.containsKey(aId)) {
				   scoreAuthor.put(aId, 0);
			   }
			   int prv_score_author = scoreAuthor.get(aId);
			   scoreAuthor.put(aId,scoreLettre+prv_score_author);
		   }
		   b = b.getPred();
	   }
	   
	   int score_max_author = 0;
	   String max_author = "";
	   int score_max_poli = 0;
	   String max_poli = "";
	   for(Entry<String,Integer> e: scorePoli.entrySet()) {
		   if(e.getValue() > score_max_poli) {
			   score_max_poli = e.getValue();
			   max_poli = e.getKey();
		   }
	   }
	   
	   for(Entry<String,Integer> e: scoreAuthor.entrySet()) {
		   if(e.getValue() > score_max_author) {
			   score_max_author = e.getValue();
			   max_author = e.getKey();
		   }
	   }
	   
	   System.out.println("Le politicien ayant obtenu le meilleur score est " + 
  				max_poli + " avec un score de " + score_max_poli);
	   System.out.println("L'auteur ayant obtenu le meilleur score est " + 
			   				max_author + " avec un score de " + score_max_author);
   }

    public static void main(String args[]) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, JSONException {
        ServerSocket serverSocket = null;
        Socket socket = null;
        
        //"fake politician" just to get the blockchain
        final String serverHost = args[0];
        final int port = Integer.valueOf(args[1]);
        p = new Politicien(serverHost, port,true);
        p.listen();
        
        p.getFullWordPool();
        try {
			UtilSynchro.waitForCond(p.fullWordPool, p.fullWordPoolCond, p::isFullWordPoolAvailable, p::setFullWordPoolAvailable);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
        
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();

        }
        while (true) {
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                System.out.println("I/O error: " + e);
            }
            // new thread for a client
            new EchoThreadScoreBlockchain(socket).start();
        }
    }
}