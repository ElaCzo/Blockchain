import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    public static Mot parseFromJSON(JSONObject w) throws NumberFormatException, JSONException {
    	
    	JSONArray lettersJSON = (JSONArray) w.get("word");
    	List<Lettre> letters = new ArrayList<Lettre>();
    	for(int i = 0; i < lettersJSON.length(); i++) {
    		letters.add(Lettre.parseFromJSON(lettersJSON.getJSONObject(i)));
    	}
    	byte[] head = Util.hexStringToByteArray(w.getString("head").toString());
    	byte[] politician = Util.hexStringToByteArray(w.get("politician").toString());
    	byte[] sig = Util.hexStringToByteArray(w.getString("signature").toString());
    	return new Mot(letters, head, politician, sig);
    }
    
    public byte[] toByte() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        for(Lettre l : letters)
            outputStream.write(l.toByte());
        outputStream.write(head);
        outputStream.write(politician);
        return outputStream.toByteArray();
    }
    
    public byte[] hash() throws NoSuchAlgorithmException, IOException {
        byte[] hashed = Sha.hash_sha256(toByte());
        return hashed;
    }
    
    public int getScore() {
    	int score = 0;
    	for (Lettre l: letters) {
    		score += l.scrableValue();
    	}
    	return score;
    }
    
    public String toHex() throws NoSuchAlgorithmException, IOException {
    	return Util.bytesToHex(toByte());
    }
    

    public boolean isValid() {
    	for(int i = 0; i < letters.size(); i++) {
        	for(int j = i+1; j < letters.size(); j++) {
        		Lettre letter_i = letters.get(i);
        		Lettre letter_j = letters.get(j);
        		if(!Arrays.equals(letter_i.getHead(), letter_j.getHead())) return false;
        		if(Arrays.equals(letter_i.getPublicAuthorKey(), letter_j.getPublicAuthorKey())) return false;
        	}
    	}
    	return true;
    }
    
    public String headHex() {
    	return Util.bytesToHex(head);
    }
    
    public String toString() {
    	try {
			return toJSON().toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
    }
    
    public boolean isEmptyWord() {
    	return false;
    }

}
