import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.InvalidKeyException;
import java.security.SignatureException;


public class Client {
    protected Socket socketOfClient;
    protected DataOutputStream os;
    protected DataInputStream is;
    
    protected Socket socketOfDict;
    protected DataOutputStream osDict;
    protected DataInputStream isDict;

    protected long period;
    
    protected ReentrantLock lockNextPeriod = new ReentrantLock();
    protected Condition isNextPeriodCondition = lockNextPeriod.newCondition();
    protected boolean isNextPeriod = false;
    public boolean isNextPeriod() {
		return isNextPeriod;
	}

	public void setNextPeriod(boolean isNextPeriod) {
		this.isNextPeriod = isNextPeriod;
	}

    protected boolean traitementMessage(String msg) throws JSONException, NoSuchAlgorithmException, IOException {
        if (Messages.isNextTurn(msg)) {
            period = Messages.nextTurn(msg);
			UtilSynchro.notifyCond(lockNextPeriod, isNextPeriodCondition, this::setNextPeriod);
            return true;
        }
        else if(Messages.isInjectRawOP(msg)){
            System.out.println("Message reçu du serveur : "
                    + Messages.injectRawOP(msg));
            return true;
        }

        return false;
    }

    public void readingInChanel(){
        Thread recevoir = new Thread(new Runnable() {
            @Override
            public void run() {
                String msg;
                try {
                    do {
                        msg = Util.readMsg(is);
                        if(traitementMessage(msg));
                        else{
                            System.out.println("Commande serveur non reconnue pour le message " +msg);
                        }
                    }while(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                System.out.println("Serveur déconnecté");
                try {
                    closeConnection();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        recevoir.start();
    }

    public Client (String serverHost, int port) throws JSONException, UnknownHostException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        // Send a request to connect to the server is listening
        // on machine 'localhost' port 9999.
        socketOfClient = new Socket(serverHost, port);
        
        socketOfDict = new Socket(serverHost, DicoServer.PORT);
        osDict = new DataOutputStream(socketOfDict.getOutputStream());
        isDict = new DataInputStream(socketOfDict.getInputStream());

        // Create output stream at the client (to send data to the server)
        os = new DataOutputStream(socketOfClient.getOutputStream());

        // Input stream at Client (Receive data from the server).
        is = new DataInputStream(socketOfClient.getInputStream());


        readingInChanel();
    }

    public void closeConnection() throws IOException {
    	os.close();
    	is.close();
    	socketOfClient.close();
    }

    public void listen() throws JSONException {
        JSONObject listen = new JSONObject();
        listen.put("listen", JSONObject.NULL);
        Util.writeMsg(os, listen);
    }

    public void stopListen() throws JSONException {
        JSONObject stopListen = new JSONObject();
        stopListen.put("stop_listen", JSONObject.NULL);
        Util.writeMsg(os, stopListen);
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
    
    protected ReentrantLock fullLetterPool = new ReentrantLock();
    protected Condition fullLetterPoolCond = fullLetterPool.newCondition();
    protected boolean fullLetterPoolAvailable = false;

	public boolean isFullLetterPoolAvailable() {
		return fullLetterPoolAvailable;
	}

	public void setFullLetterPoolAvailable(boolean fullLetterPoolAvailable) {
		this.fullLetterPoolAvailable = fullLetterPoolAvailable;
	}

	public static void main(String[] args) throws UnknownHostException, JSONException, IOException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        if(args.length!=2) {
            System.out.println("usage : command serveur port");
            System.exit(-1);
        }
        // Server Host
        final String serverHost = args[0];
        final int port = Integer.valueOf(args[1]);
        Client c = new Client(serverHost, port);
    }
}