package Pair2Pair;

public class Lettre {
    public char c;
    public String hash;
    public int auteurId;

    public Lettre(char c, int auteurId) {
        this.c = c;
        this.auteurId = auteurId;
        this.hash = hash();
    }

    public String hash() {
        try {

            String hash = Sha.Sha256(new String(c + "" + auteurId));
        } catch (Exception e) {
            System.out.println("Erreur SHA dans LETTRE");
        }

        return hash;

    }

}
