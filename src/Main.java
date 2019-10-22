import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Main
 */

public class Main {

    public static void main(String[] args) {
        System.out.println("hello");
        try {
            URL url = new URL("http://localhost");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}