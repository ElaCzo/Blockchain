package Pair2Pair;

import java.util.ArrayList;

/**
 * Pair
 */
public abstract class Pair extends Thread {

    protected ArrayList<Integer> messagesRecus;

    protected ArrayList<Pair> liens;

    protected Block blockchain;

    protected static int cpt = 0;
    protected int id;

    public Pair() {
        liens = new ArrayList<>();
        messagesRecus = new ArrayList<>();
        id = cpt;
        cpt++;
        blockchain = null;
        // liensTreashold(6);
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

    public boolean liensTreashold(int threashold) {
        if (liens.isEmpty()) {
            return false;

        }

        if (liens.size() >= threashold) {
            return true;
        }

        for (Pair pair : liens) {
            if (!pair.getLiens().isEmpty()) {

                for (Pair p : pair.getLiens()) {
                    if (!liens.contains(p)) {
                        System.out.println(" pair : " + id + " add : " + p.getPairId());
                        liens.add(p);
                        if (!p.getLiens().contains(this)) {
                            System.out.println(" pair : " + p.getPairId() + " add : " + id);
                            p.addLien(this);

                        }
                    }
                }
            }
        }

        return true;

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

    @Override
    public abstract void run();

}