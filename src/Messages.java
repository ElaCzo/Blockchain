import org.json.JSONException;

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


}
