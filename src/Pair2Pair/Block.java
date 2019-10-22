package Pair2Pair;

import java.util.ArrayList;

public class Block {
    private String mot;
    private String hash;
    private Block suivant;
    private int AuteutID;

    ArrayList<Lettre> lettres = new ArrayList<>();

    public Block(String mot, String hash, int AuteutID) {
        this.mot = mot;
        this.hash = hash;
        this.AuteutID = AuteutID;
        suivant = null;
    }

    public Block(String mot, String hash, Block precedent, int AuteutID) {
        this(mot, hash, AuteutID);
        precedent.suivant = this;
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
     * @return the suivant
     */
    public Block getSuivant() {
        return suivant;
    }

    /**
     * @return the auteutID
     */
    public int getAuteutID() {
        return AuteutID;
    }

    public String hash() {
        try {
            String hash = Sha.Sha256(new String(mot + "" + AuteutID));
        } catch (Exception e) {
            System.out.println("Erreur SHA dans BLOCK");
        }

        return hash;

    }
}
