import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MotVide extends Mot {

	private MotVide(List<Lettre> letters, byte[] head, byte[] politician, byte[] sig) {
		super(letters, head, politician, sig);
		// TODO Auto-generated constructor stub
	}
	
	public MotVide() {
		super(null, null, null,null);
	}
	
	@Override
	public byte[] hash() throws NoSuchAlgorithmException {
		return Sha.hash_sha256("");
	}
	
	@Override
	public int getScore() {
		return 0;
	}
	
	@Override
    public JSONObject toJSON() throws JSONException {
    	JSONObject word = new JSONObject();
        word.put("emptyword", JSONObject.NULL);
        return word;
       
    }
	
	@Override
    public boolean isEmptyWord() {
    	return true;
    }

}
