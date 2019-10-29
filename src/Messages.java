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

    public static List<String> fullLetterPool(String msgJSON){
        return Util.JSONArrayToList(Util.parseJSONObjectFromString(msgJSON).getJSONArray("full_letterpool"));
    }

    public static boolean isFullLetterPool(String msgJSON) throws JSONException {
        return Util.parseJSONObjectFromString(msgJSON).has("full_letterpool");
    }

    public static List<String> fullWordPool(String msgJSON){
        return Util.JSONArrayToList(Util.parseJSONObjectFromString(msgJSON).getJSONArray("full_wordpool"));
    }

    public static boolean isFullWordPool(String msgJSON) throws JSONException {
        return Util.parseJSONObjectFromString(msgJSON).has("full_wordpool");
    }

    public static List<String> diffWordPool(String msgJSON){
        return Util.JSONArrayToList(Util.parseJSONObjectFromString(msgJSON).getJSONArray("diff_wordpool"));
    }

    public static boolean isDiffWordPool(String msgJSON) throws JSONException {
        return Util.parseJSONObjectFromString(msgJSON).has("diff_wordpool");
    }

    public static List<String> diffLetterPool(String msgJSON){
        return Util.JSONArrayToList(Util.parseJSONObjectFromString(msgJSON).getJSONArray("diff_letterpool"));
    }

    public static boolean isDiffLetterPool(String msgJSON) throws JSONException {
        return Util.parseJSONObjectFromString(msgJSON).has("diff_letterpool");
    }


}
