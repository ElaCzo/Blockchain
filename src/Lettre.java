import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import jdk.nashorn.api.scripting.JSObject;

public class Lettre {
    private String l;
    private long period;
    private byte[] head;
    private byte[] publicAuthorKey;
    private byte[] sig;
	public Lettre(String l, long period, byte[] head, byte[] publicAuthorKey, byte[] sig) {
		super();
		this.l = l;
		this.period = period;
		this.head = head;
		this.publicAuthorKey = publicAuthorKey;
		this.sig = sig;
	}
	public String getL() {
		return l;
	}
	public long getPeriod() {
		return period;
	}
	public byte[] getHead() {
		return head;
	}
	public byte[] getPublicAuthorKey() {
		return publicAuthorKey;
	}
	public byte[] getSig() {
		return sig;
	}
    
    public JSONObject toJSON() throws JSONException {
        JSONObject letter = new JSONObject();
        letter.put("letter", l);
        letter.put("period", period);
        letter.put("head", Util.bytesToHex(head));
        letter.put("author", Util.bytesToHex(publicAuthorKey));
        letter.put("signature", Util.bytesToHex(sig));
        return letter;
       
    }
    
    public String headHex() {
    	return Util.bytesToHex(head);
    }
    
    @Override
    public String toString() {
    	try {
			return toJSON().toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
    }
    
    public byte[] toByte() throws IOException {
    	ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
    	outputStream.write( l.getBytes() );
    	outputStream.write(Util.longToBytes(period));
    	outputStream.write(head);
    	outputStream.write(publicAuthorKey);
    	outputStream.write(sig);
    	return outputStream.toByteArray();
    }
    
    public static Lettre parseFromJSON(JSONObject l) throws NumberFormatException, JSONException {
    	
    	String lettre = l.get("letter").toString();
    	long period = Long.parseLong(l.get("period").toString());
    	byte[] head = Util.hexStringToByteArray(l.getString("head").toString());
    	byte[] public_key = Util.hexStringToByteArray(l.get("author").toString());
    	byte[] sig = Util.hexStringToByteArray(l.getString("signature").toString());
    	return new Lettre(lettre, period, head, public_key, sig);
    }
    
    public boolean hasSamePeriodAndAuthor(Lettre l2) {
    	if(this.getPeriod() == l2.getPeriod()) {
    		//all keys have the same length
    		for(int i = 0; i < this.getPublicAuthorKey().length; i++) {
    			if(l2.getPublicAuthorKey()[i] != publicAuthorKey[i]) {
    				return false;
    			}
    		}
    	}
    	else {
    		return false;
    	}
    	return true;
    }
    
    public String authorId() {
    	return Util.bytesToHex(publicAuthorKey);
    }
    
    public int scrableValue() {
        int value = 0;
        switch (l.charAt(0)) {
        case 'a':
            value += 1;
            break;
        case 'b':
            value += 3;
            break;
        case 'c':
            value += 3;
            break;
        case 'd':
            value += 2;
            break;
        case 'e':
            value += 1;
            break;
        case 'f':
            value += 4;
            break;
        case 'g':
            value += 2;
            break;
        case 'h':
            value += 4;
            break;
        case 'i':
            value += 1;
            break;
        case 'j':
            value += 8;
            break;
        case 'k':
            value += 10;
            break;
        case 'l':
            value += 1;
            break;

        case 'm':
            value += 2;
            break;

        case 'n':
            value += 1;
            break;

        case 'o':
            value += 1;
            break;

        case 'p':
            value += 3;
            break;

        case 'q':
            value += 8;
            break;

        case 'r':
            value += 1;
            break;

        case 's':
            value += 1;
            break;

        case 't':
            value += 1;
            break;

        case 'u':
            value += 1;
            break;

        case 'v':
            value += 4;
            break;

        case 'w':
            value += 10;
            break;

        case 'x':
            value += 10;
            break;

        case 'y':
            value += 10;
            break;

        case 'z':
            value += 10;
            break;

        default:
            break;
        }
        return value;
    }

}
