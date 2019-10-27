import org.json.JSONException;
import org.json.JSONObject;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.List;
import java.util.Scanner;


public class Client {

    private Socket socketOfClient;
    private DataOutputStream os;
    private DataInputStream is;
    private List<String> letter_pool;
    private PrivateKey private_key;
    private PublicKey public_key;
    private String tmpKey;
    
    public Client (String serverHost, int port) throws JSONException, UnknownHostException, IOException, NoSuchAlgorithmException, NoSuchProviderException {

        // Send a request to connect to the server is listening
        // on machine 'localhost' port 9999.
        socketOfClient = new Socket(serverHost, port);

        // Create output stream at the client (to send data to the server)
        os = new DataOutputStream(socketOfClient.getOutputStream());

        // Input stream at Client (Receive data from the server).
        is = new DataInputStream(socketOfClient.getInputStream());
    	
    	
        ED25519 ed = new ED25519();
        KeyPair kp = ed.genKeys();
        EdDSAPublicKey pk = ((EdDSAPublicKey) kp.getPublic());
        System.out.println(Util.bytesToHex(pk.getAbyte()));
        
        JSONObject reg = new JSONObject();
        tmpKey = "b7b597e0d64accdb6d8271328c75ad301c29829619f4865d31cc0c550046a08f";
        reg.put("register", tmpKey);
        Util.writeMsg(os, reg);
        String msgread = Util.readMsg(is);
        letter_pool = Util.JSONArrayToList(Util.parseJSONObjectFromString(msgread).get("letters_bag"));
    }
    
    public void listen() throws JSONException {
        JSONObject listen = new JSONObject();
        listen.put("listen", JSONObject.NULL);
        Util.writeMsg(os, listen);
        
    }
    
    public void injectLetter(String c) throws JSONException {
    	JSONObject letter = new JSONObject();
    	letter.put("letter", "a");
    	letter.put("period", 0);
    	letter.put("head", "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
    	letter.put("author", tmpKey);
    	letter.put("signature", "8b6547447108e11c0092c95e460d70f367bc137d5f89c626642e1e5f2ceb6108043d4a080223b467bb810c52b5975960eea96a2203a877f32bbd6c4dac16ec07");
    	Util.writeMsg(os, letter);
    }
    
    public void closeConnection() throws IOException {
    	os.close();
    	is.close();
    	socketOfClient.close();
    }
    
    public static void main(String[] args) throws UnknownHostException, JSONException, IOException, NoSuchAlgorithmException, NoSuchProviderException {

        if(args.length!=2) {
            System.out.println("usage : command serveur port");
            System.exit(-1);
        }
        // Server Host
        final String serverHost = args[0];
        final int port = Integer.valueOf(args[1]);
        Client c = new Client(serverHost, port);
        
        
        c.listen();
        c.injectLetter(c.letter_pool.remove(0));
        while(true) {
        	
        }

    }

}