import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Messages {
    public static int nextTurn (String msgJSON) throws JSONException {
        return Util.parseJSONObjectFromString(msgJSON).getInt("next_turn");
    }

    public static boolean isNextTurn(String msgJSON) throws JSONException {
        return Util.parseJSONObjectFromString(msgJSON).has("next_turn");
    }

    public static boolean isLettersBag(String msgJSON) throws JSONException {
        return Util.parseJSONObjectFromString(msgJSON).has("letters_bag");
    }

    public static List<String> lettersBag(String msgJSON) throws JSONException {
        return Util.JSONArrayToList(Util.parseJSONObjectFromString(msgJSON).getJSONArray("letters_bag"));
    }

    public static List<Lettre> fullLetterPool(String msg) throws JSONException {
    	JSONObject msgJSON = Util.parseJSONObjectFromString(msg);
    	JSONObject fullLetterPoolJSON = (JSONObject) msgJSON.get("full_letterpool");

    	JSONArray letters = (JSONArray) fullLetterPoolJSON.get("letters");
    	List<Lettre> res = new ArrayList<Lettre>();
    	for(int i = 0 ; i < letters.length(); i++){
    		JSONArray letter = (JSONArray) letters.get(i);
    		res.add(Lettre.parseFromJSON(letter.getJSONObject(1)));
    	}
    	return res;
    }

    public static boolean isFullLetterPool(String msgJSON) throws JSONException {
        return Util.parseJSONObjectFromString(msgJSON).has("full_letterpool");
    }

    public static List<String> fullWordPool(String msgJSON) throws JSONException {
        return Util.JSONArrayToList(Util.parseJSONObjectFromString(msgJSON).getJSONArray("full_wordpool"));
    }

    public static boolean isFullWordPool(String msgJSON) throws JSONException {
        return Util.parseJSONObjectFromString(msgJSON).has("full_wordpool");
    }

    public static List<String> diffWordPool(String msgJSON) throws JSONException {
        return Util.JSONArrayToList(Util.parseJSONObjectFromString(msgJSON).getJSONArray("diff_wordpool"));
    }

    public static boolean isDiffWordPool(String msgJSON) throws JSONException {
        return Util.parseJSONObjectFromString(msgJSON).has("diff_wordpool");
    }

    public static List<String> diffLetterPool(String msgJSON) throws JSONException {
        return Util.JSONArrayToList(Util.parseJSONObjectFromString(msgJSON).getJSONArray("diff_letterpool"));
    }

    public static boolean isDiffLetterPool(String msgJSON) throws JSONException {
        return Util.parseJSONObjectFromString(msgJSON).has("diff_letterpool");
    }

    public static List<String> injectRawOP(String msgJSON) throws JSONException {
        return Util.JSONArrayToList(Util.parseJSONObjectFromString(msgJSON).getJSONArray("inject_raw_op"));
    }

    public static boolean isInjectRawOP(String msgJSON) throws JSONException {
        return Util.parseJSONObjectFromString(msgJSON).has("inject_raw_op");
    }
    
    public static boolean isInjectLetter(String msgJSON) throws JSONException {
        return Util.parseJSONObjectFromString(msgJSON).has("inject_letter");
    }
    
    public static Lettre letter(String msgJSON) throws JSONException {
    	JSONObject letter = (JSONObject) Util.parseJSONObjectFromString(msgJSON).get("inject_letter");
        return Lettre.parseFromJSON(letter);
    }
    
    public static boolean isInjectWord(String msgJSON) throws JSONException {
        return Util.parseJSONObjectFromString(msgJSON).has("inject_word");
    }
    
    public static Mot word(String msgJSON) throws JSONException {
    	JSONObject word = (JSONObject) Util.parseJSONObjectFromString(msgJSON).get("inject_word");
        return Mot.parseFromJSON(word);
    }
}
