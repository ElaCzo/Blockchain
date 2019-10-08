import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    static String exemple;
    public static void main(String[] args) {

        if(args.length!=2) {
            System.out.println("usage : command serveur port");
            System.exit(-1);
        }
        // Server Host
        final String serverHost = args[0];

        Socket socketOfClient = null;
        BufferedWriter os = null;
        BufferedReader is = null;

        try {

            // Send a request to connect to the server is listening
            // on machine 'localhost' port 9999.
            socketOfClient = new Socket(serverHost, Integer.valueOf(args[1]));

            // Create output stream at the client (to send data to the server)
            os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));


            // Input stream at Client (Receive data from the server).
            is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + serverHost);
            return;
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + serverHost);
            return;
        }

        try {
            // CODER ICI //
            @SuppressWarnings("resource")
            Scanner sc = new Scanner(System.in);
            exemple = sc.nextLine();

            // Write data to the output stream of the Client Socket.
            os.write("CONNECT/"+ exemple +"/\n");
            // Flush data.
            os.flush();

            // Read data sent from the server.
            // By reading the input stream of the Client Socket.

            // CODE LECTURE DATA DU SERVEUR ICI

            os.close();
            is.close();
            socketOfClient.close();
        } catch (UnknownHostException e) {
            System.err.println("Trying to connect to unknown host: " + e);
        } catch (IOException e) {
            System.err.println("IOException:  " + e);
        }
    }

}