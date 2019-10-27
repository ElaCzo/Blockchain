import org.json.JSONException;
import org.json.JSONObject;

import net.i2p.crypto.eddsa.EdDSAPrivateKey;
import net.i2p.crypto.eddsa.EdDSAPublicKey;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
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
    
    public Client (String serverHost, int port) throws JSONException, UnknownHostException, IOException, NoSuchAlgorithmException, NoSuchProviderException {

        // Send a request to connect to the server is listening
        // on machine 'localhost' port 9999.
        socketOfClient = new Socket(serverHost, port);

        // Create output stream at the client (to send data to the server)
        os = new DataOutputStream(socketOfClient.getOutputStream());

        // Input stream at Client (Receive data from the server).
        is = new DataInputStream(socketOfClient.getInputStream());
    	
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