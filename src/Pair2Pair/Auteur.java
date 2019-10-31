package Pair2Pair;

import java.util.ArrayList;
import java.util.Random;

/**
 * Auteur
 */
public class Auteur extends Pair {

    private ArrayList<Character> pool;

    private int diff = 12;

    public Auteur() {
        super();
        this.pool = null;
    }

    public Auteur(ArrayList<Character> pool) {
        super();
        this.pool = new ArrayList<>(pool);
    }

    public int getScore() {
        return blockchain.auteurScore(id);
    }

    /**
     * @return the pool
     */
    public ArrayList<Character> getPool() {
        return pool;
    }

    private void generateLettre() {

        Random rnd = new Random();
        int random = rnd.nextInt(pool.size());
        int nonce = 0;
        Lettre l = new Lettre(pool.get(random), id, blockchain.getLastHash(), nonce);
        while (Integer.numberOfLeadingZeros(l.getHash()) < diff) {
            nonce++;
            l = new Lettre(pool.get(random), id, blockchain.getLastHash(), nonce);

        }
        sendMessage(new Message(l, id));
    }

    @Override
    public void sendMessage(Message m) {

        if (!getMessagesRecus().contains(m.getId())) {

            addMessageId(m.getId());
            switch (m.getType()) {
            case STRING:
                String message = m.getContenu();
                System.out.println("AUTEUR : " + id + " receive " + message + " from " + m.getAuteurID() + " ( MID : "
                        + m.getId() + " ) ");

                for (Pair pair : liens) {
                    if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                        pair.sendMessage(m);
                    }
                }
                break;

            case POOLLETTRE:

                System.out.println("AUTEUR : " + id + " receive letter pool from " + m.getAuteurID() + " ( MID : "
                        + m.getId() + " ) ");
                if (pool == null) {

                    this.pool = new ArrayList<>(m.getPool());
                    for (Pair pair : liens) {
                        if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                            pair.sendMessage(m);
                        }
                    }

                    synchronized (this) {

                        this.notifyAll();
                    }

                }
                break;
            case LETTRE:
                // System.out.println("AUTEUR : " + id + " receive " + m.getLettre().getC() +
                // "from " + m.getAuteurID()
                // + " ( MID : " + m.getId() + " ) ");

                for (Pair pair : liens) {
                    if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                        pair.sendMessage(m);
                    }
                }

                break;

            case BLOCK:

                if (fini) {
                    // System.out.println("Auteur : " + id + " receive block from " +
                    // m.getAuteurID() + " | size : "
                    // + m.getBlock().size() + " ( MID : " + m.getId() + " ) ");

                } else {

                    if (m.getBlock().isValid()) {

                            int value = blockchain.value();

                            if (value <= m.getBlock().value()) {
                                blockchain = m.getBlock();
                                if (blockchain.size() == 20 && fini == false) {

                                    for (Pair pair : liens) {

                                        pair.sendMessage(new Message(m.getBlock()));
                                        fini = true;

                                    }
                                    return;
                                }
                            }

                            for (Pair pair : liens) {

                                pair.sendMessage(m);
                            }
                    }

                }
                break;

            case FINI:

                if (fini) {

                    for (Pair pair : liens) {
                        if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                            pair.sendMessage(m);

                        }

                    }
                    return;

                }

                System.out.println("Auteur : " + id + " receive Fin from " + m.getAuteurID() + " nb lettres "
                        + m.getBlock().getChars().size() + " ( MID : " + m.getId() + " ) ");

                if (m.getBlock().isValid() && blockchain.value() <= m.getBlock().value() && !fini) {
                    for (Pair pair : liens) {
                        if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                            pair.sendMessage(m);
                        }

                    }
                    fini = true;
                }
                break;

            default:
                break;
            }
        }

    }

    public void run() {
        try {
            System.out.println("AUTEUR : " + id + " start");

            if (pool != null) {
                for (Pair pair : liens) {
                    pair.sendMessage(new Message(pool, id));
                }
            } else {
                while (pool == null) {

                    synchronized (this) {
                        this.wait();
                    }
                }
            }
            System.out.println("AUTEUR : " + id + " a reçu le pool");
        } catch (InterruptedException e) {
        }

        while (!fini) {

            generateLettre();
        }

        System.out.println("Auteur : " + id + " a fini | B taille : " + blockchain.size());
    }

}