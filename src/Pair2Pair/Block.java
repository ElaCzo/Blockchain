package Pair2Pair;

import java.util.ArrayList;

public class Block {
    private String mot;
    private int hash;
    private Block suivant;
    private int AuteurID;

    ArrayList<Lettre> lettres = new ArrayList<>();

    public Block(String mot, int hash, int AuteurID) {
        this.mot = mot;
        this.hash = hash;
        this.AuteurID = AuteurID;
        suivant = null;
    }

    public Block(String mot, int hash, Block precedent, int AuteurID) {
        this(mot, hash, AuteurID);
        precedent.suivant = this;
    }

    /**
     * @return the hash
     */
    public int getHash() {
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
    public int getAuteurID() {
        return AuteurID;
    }

    public void hash() {
        try {

            int nhash = Sha.Sha256(new String(mot + "" + AuteurID));
            this.hash = nhash;
        } catch (Exception e) {
            System.out.println("Erreur SHA dans LETTRE");
        }

    }
}
