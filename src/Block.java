import java.util.ArrayList;

public class Block {
    private String mot;
    private String hash;
    private int AuteurID;
    private String hashPredecesseur;
    private int nonce;

    ArrayList<Lettre> lettres;

    public Block(String mot, ArrayList<Lettre> lettres, int AuteurID, int nonce) {
        this.mot = mot;
        this.lettres = new ArrayList<>(lettres);
        this.AuteurID = AuteurID;
        this.nonce = nonce;
        hash();
    }

    public Block(String mot, ArrayList<Lettre> lettres, int AuteurID, String hashPredecesseur, int nonce) {
        this.mot = mot;
        this.lettres = new ArrayList<>(lettres);
        this.AuteurID = AuteurID;
        this.hashPredecesseur=hashPredecesseur;
        this.nonce = nonce;
        hash(hashPredecesseur);
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @return the lettres
     */
    public ArrayList<Lettre> getLettres() {
        return lettres;
    }

    /**
     * @return the mot
     */
    public String getMot() {
        return mot;
    }

    /**
     * @return the auteutID
     */
    public int getAuteurID() {
        return AuteurID;
    }

    /**
     * @return the nonce
     */
    public int getNonce() {
        return nonce;
    }

    public void hash() {
        try {
            byte[] nhash = Sha.hash_sha256(new String(mot + "" + AuteurID + "" + nonce));
            this.hash = Util.bytesToString(nhash);
        } catch (Exception e) {
            System.out.println("Erreur SHA dans LETTRE");
        }
    }

    public void hash(String hashPredecesseur) {
        try {
            byte[] nhash = Sha.hash_sha256(new String(mot + AuteurID + hashPredecesseur + nonce));
            this.hash = Util.bytesToString(nhash);
        } catch (Exception e) {
            System.out.println("Erreur SHA dans LETTRE");
        }
    }
}
