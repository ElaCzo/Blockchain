import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Mot {
	private List<Lettre> letters;
	private byte[] head;
    private byte[] politician;
    private byte[] sig;
    
	public Mot(List<Lettre> letters, byte[] head, byte[] politician, byte[] sig) {
		super();
		this.letters = letters;
		this.head = head;
		this.politician = politician;
		this.sig = sig;
	}
    

	public List<Lettre> getLetters() {
		return letters;
	}
	public byte[] getHead() {
		return head;
	}
	public byte[] getPolitician() {
		return politician;
	}
	public byte[] getSig() {
		return sig;
	}
	
    public JSONObject toJSON() throws JSONException {
    	JSONObject word = new JSONObject();
        JSONArray lettersJSON = new JSONArray();
        for(Lettre l: letters) {
        	lettersJSON.put(l.toJSON());
        }
        word.put("word", lettersJSON);
        word.put("head", Util.bytesToHex(head));
        word.put("politician", Util.bytesToHex(politician));
        word.put("signature", Util.bytesToHex(sig));
        return word;
       
    }

}
