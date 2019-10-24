package Pair2Pair;

import java.util.ArrayList;

public class Block {

    private String mot;
    private int hash;
    private int AuteurID;
    private int nonce;

    ArrayList<Lettre> lettres;

    public Block(String mot, ArrayList<Lettre> lettres, int AuteurID, int nonce) {
        this.mot = mot;
        this.lettres = new ArrayList<>(lettres);
        this.AuteurID = AuteurID;
        this.nonce = nonce;
        hash();
    }

    // public boolean isValid() {
    // ArrayList<Lettre> l = new ArrayList<>();
    // Block blo = this;
    // while (blo.floor == false) {
    // for (Lettre lettre : lettres) {
    // if (l.contains(lettre)) {
    // return false;
    // }
    // l.add(lettre);
    // }
    // char[] m = mot.toCharArray();
    // if (m.length != lettres.size()) {
    // return false;
    // }
    // for (int i = 0; i < m.length; i++) {
    // if (m[i] != lettres.get(i).getC()) {
    // return false;
    // }

    // }
    // blo = blo.getPred();
    // }
    // return true;

    // }

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
     * @return the auteutID
     */
    public int getAuteurID() {
        return AuteurID;
    }

    /**
     * @return the length
     */

    /**
     * @return the nonce
     */
    public int getNonce() {
        return nonce;
    }

    public void hash() {
        try {

            int nhash = Sha.Sha256(new String(mot + "" + AuteurID + "" + nonce));
            this.hash = nhash;
        } catch (Exception e) {
            System.out.println("Erreur SHA dans LETTRE");
        }

    }

    // public void print() {
    // Block blo = this;
    // while (blo.floor == false) {
    // System.out.println(mot + " size " + length);
    // blo = blo.getPred();
    // }
    // }
}
