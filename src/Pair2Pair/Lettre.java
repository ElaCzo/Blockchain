package Pair2Pair;

import java.util.ArrayList;

public class Lettre {
    private int nonce;
    private char c;
    private int hash;
    private int auteurId;
    private int blockhash;

    public Lettre(char c, int auteurId, int bhash, int nonce) {
        this.c = c;
        this.auteurId = auteurId;
        this.blockhash = bhash;
        this.nonce = nonce;
        hash();
    }

    public void hash() {
        try {

            int nhash = Sha.Sha256(new String(c + "" + auteurId + "" + nonce + "" + blockhash));
            this.hash = nhash;
        } catch (Exception e) {
            System.out.println("Erreur SHA dans LETTRE");
        }

    }

    /**
     * @return the blockhash
     */
    public int getBlockhash() {
        return blockhash;
    }

    public static boolean contain(int auteurId, ArrayList<Lettre> list) {
        for (Lettre lettre : list) {
            if (lettre.auteurId == auteurId)
                return true;
        }
        return false;
    }

    /**
     * @return the auteurId
     */
    public int getAuteurId() {
        return auteurId;
    }

    /**
     * @return the c
     */
    public char getC() {
        return c;
    }

    /**
     * @return the hash
     */
    public int getHash() {
        return hash;
    }

}
