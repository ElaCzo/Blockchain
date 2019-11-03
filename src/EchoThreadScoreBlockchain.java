import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class EchoThreadScoreBlockchain extends Thread {
    protected Socket socket;
    protected boolean is_auteur = false;

    public EchoThreadScoreBlockchain(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        DataInputStream inp = null;
        DataOutputStream out = null;
        try {
            inp = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }
        String line;
        while (true) {
            try {
                line = inp.readUTF();
                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    return;
                } else {
                	if(line.equals("author")) {
                		is_auteur = true;
                		ScoreBlockchainServer.incNbAuthor();
                	}
                	else if (line.equals("politician")){
                		ScoreBlockchainServer.incNbPoli();
                		ScoreBlockchainServer.registerPoli(out);
                	}
                	else if (line.equals("quit author")){
                		ScoreBlockchainServer.decNbAuthor();
                		return;
                	}
                	else if (line.equals("quit politician")){
                		ScoreBlockchainServer.decNbPoli();
                		return;
                	}
                	else if (line.equals("over?")){
                		out.writeBoolean(ScoreBlockchainServer.continueOrNot());
                	}
                    out.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}