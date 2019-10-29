import Pair2Pair.Message;
import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONException;
import org.json.JSONObject;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.nio.CharBuffer;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.List;
import java.util.Scanner;


public class Client {
    protected Socket socketOfClient;
    protected DataOutputStream os;
    protected DataInputStream is;

    protected int tour;

    private ArrayList<Lettre> lettres;

    public void readingInChanel(){
        Thread recevoir = new Thread(new Runnable() {
            @Override
            public void run() {
                String msg;
                try {
                    do {
                        msg = Util.readMsg(is);
                        if (Messages.isNextTurn(msg)) {
                            tour = Messages.nextTurn(msg);
                        }
                        else if(Messages.isFullLetterPool(msg)){
                            Messages.fullLetterPool(msg);
                        }
                        else if(Messages.isDiffLetterPool(msg)){
                            Messages.diffLetterPool(msg);
                        }
                        else if(Messages.isFullWordPool(msg)){
                            Messages.fullWordPool(msg);
                        }
                        else if(Messages.isDiffWordPool(msg)){
                            Messages.diffWordPool(msg);
                        }
                        else{
                            System.out.println("Commande serveur non reconnue.");
                        }
                    }while(true);
                } catch (JSONException e) {
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

        // Create output stream at the client (to send data to the server)
        os = new DataOutputStream(socketOfClient.getOutputStream());

        // Input stream at Client (Receive data from the server).
        is = new DataInputStream(socketOfClient.getInputStream());

        lettres = new ArrayList<Lettre>();

        readingInChanel();
    }


    public void closeConnection() throws IOException {
    	os.close();
    	is.close();
    	socketOfClient.close();
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