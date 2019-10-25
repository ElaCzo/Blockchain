package Pair2Pair;

import java.util.ArrayList;

/**
 * Message
 */

public class Message {

    private TypeMessage type = null;
    private String contenu = null;
    private int auteurID;
    private ArrayList<Character> pool = null;
    private Lettre lettre = null;
    private Blockchain block = null;

    private static int cpt = 0;
    private int id;

    public Message() {

        synchronized ((Integer) cpt) {

            this.id = cpt;
            cpt++;
        }
    }

    public Message(String contenu, int auteurID) {
        this();
        this.type = TypeMessage.STRING;
        this.contenu = contenu;
        this.auteurID = auteurID;

    }

    public Message(ArrayList<Character> pool, int auteurID) {
        this();
        this.type = TypeMessage.POOLLETTRE;
        this.auteurID = auteurID;
        this.pool = pool;
    }

    public Message(Lettre lettre, int auteurID) {
        this();
        this.type = TypeMessage.LETTRE;
        this.auteurID = auteurID;
        this.lettre = lettre;
    }

    public Message(Blockchain bloc, int auteurID) {
        this();
        this.type = TypeMessage.BLOCK;
        this.auteurID = auteurID;
        this.block = new Blockchain(bloc.getChain());
    }

    public Message(Blockchain bloc) {
        this();
        this.type = TypeMessage.FINI;
        this.block = new Blockchain(bloc.getChain());
    }


    /**
     * @return the block
     */
    public Blockchain getBlock() {
        return block;
    }

    /**
     * @return the lettre
     */
    public Lettre getLettre() {
        return lettre;
    }

    /**
     * @return the pool
     */
    public ArrayList<Character> getPool() {
        return pool;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the auteurID
     */
    public int getAuteurID() {
        return auteurID;
    }

    /**
     * @return the contenu
     */
    public String getContenu() {
        return contenu;
    }

    /**
     * @return the type
     */
    public TypeMessage getType() {
        return type;
    }
}