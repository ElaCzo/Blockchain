package Pair2Pair;

import java.util.ArrayList;

import java.security.*;
import java.util.Base64;

/**
 * Pair
 */
public abstract class Pair extends Thread {

    protected int score;

    protected ArrayList<Integer> messagesRecus;

    protected ArrayList<Message> messagesAttente;

    protected ArrayList<Pair> liens;

    protected ArrayList<Block> blockchain;

    protected static int cpt = 0;
    protected int id;

    public Pair() {
        score = 0;
        liens = new ArrayList<>();
        messagesRecus = new ArrayList<>();
        id = cpt;
        cpt++;
        messagesAttente = new ArrayList<>();
        blockchain = new ArrayList<>();

    }

    public void addLien(Pair lien) {
        if (!liens.contains(lien) && lien != this) {
            liens.add(lien);
            lien.addLien(this);
        }
    }

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

    public abstract void sendMessage(Message m);

    /**
     * @return the score
     */
    public int getScore() {
        return score;
    }

    @Override
    public abstract void run();

}