package Pair2Pair;

import java.util.ArrayList;

import java.security.*;
import java.util.Base64;

/**
 * Pair
 */
public abstract class Pair extends Thread {

    protected ArrayList<Integer> messagesRecus;

    protected ArrayList<Pair> liens;

    protected Blockchain blockchain;

    protected static int cpt = 0;
    protected int id;

    protected boolean fini = false;

    public Pair() {
        liens = new ArrayList<>();
        messagesRecus = new ArrayList<>();
        id = cpt;
        cpt++;
        blockchain = new Blockchain();

    }

    public void addLien(Pair lien) {
        if (!liens.contains(lien) && lien != this) {
            liens.add(lien);
            lien.addLien(this);
        }
    }

    public abstract int getScore();

    /**
     * @return the messagesRecus
     */
    public ArrayList<Integer> getMessagesRecus() {
        return messagesRecus;
    }

    /**
     * @return the id
     */
    public int getPairId() {
        return id;
    }

    /**
     * @return the liens
     */
    public ArrayList<Pair> getLiens() {
        return liens;
    }

    public void receiveString(String s) {
        System.out.println("Pair : " + id + " receive " + s);
    }

    protected synchronized void addMessageId(int id) {

        if (messagesRecus.contains(id)) {
            return;
        }

        messagesRecus.add(id);
    }

    /**
     * @return the blockchain
     */
    public Blockchain getBlockchain() {
        return blockchain;
    }

    public abstract void sendMessage(Message m);

    @Override
    public abstract void run();

}