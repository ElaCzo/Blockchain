import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class DicoServer {

    static final int PORT = 1978;
    static private Set<String> dicto = new HashSet<String>();
    static final int MIN_SIZE = 1;
    static final int MAX_SIZE = 1;
    
    static private void parseDico(String filename) throws IOException {
    	BufferedReader bf = new BufferedReader(new FileReader(filename));
    	String line;
    	while((line = bf.readLine()) != null) {
    		dicto.add(line);
    	}
    	bf.close();
    }
    
    static boolean isInDict(String word) {
    	return dicto.contains(word);
    }

    public static void main(String args[]) throws IOException {
        ServerSocket serverSocket = null;
        Socket socket = null;
        parseDico(args[0]);
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
            new EchoThread(socket).start();
        }
    }
}