import net.i2p.crypto.eddsa.EdDSAPublicKey;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.*;
import java.util.List;

public class Politicien extends Client {
    Block block;
    private List<String> word_pool;
    private KeyPair _key;

    public Politicien(String serverHost, int port) throws JSONException, UnknownHostException, IOException, NoSuchAlgorithmException, NoSuchProviderException {
        super(serverHost, port);

        ED25519 ed = new ED25519();
        _key  = ed.genKeys();
     }

    @Override
    protected boolean traitementMessage(String msg) throws JSONException {
        if (super.traitementMessage(msg))
            return true;
        else if(Messages.isFullWordPool(msg)){
            word_pool=Messages.fullWordPool(msg);
            return true;
        }
        else if(Messages.isDiffWordPool(msg)){
            for(String w : Messages.diffWordPool(msg))
                if(!word_pool.contains(w))
                    word_pool.add(w);
            return true;
        }
        return false;
    }

    public void injectWord(List<Lettre> lettres) throws JSONException, InvalidKeyException, SignatureException, NoSuchAlgorithmException, IOException {
        byte[] public_key = ((EdDSAPublicKey)_key.getPublic()).getAbyte();
        byte[] sig = ED25519.sign(_key, Sha.hashWord(public_key, lettres, head));
        Mot m = new Mot(lettres, head, public_key, sig);
        JSONObject word = m.toJSON();
        JSONObject inject_word = new JSONObject();
        inject_word.put("inject_word", word);
        Util.writeMsg(os, inject_word);
    }
}
