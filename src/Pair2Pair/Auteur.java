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

    public void calcScore() {

        for (Block blo : blockchain) {

            if (blo.getAuteurID() == id) {

                for (Lettre let : blo.getLettres()) {
                    if (let.getAuteurId() == id) {

                        synchronized ((Integer) score) {
                            score += blo.getLettres().size();
                        }
                        break;
                    }

                }
            }
        }
    }

    @Override
    public int getScore() {
        calcScore();
        return score;
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
        Lettre l = new Lettre(pool.get(random), id, nonce);
        while (Integer.numberOfLeadingZeros(l.getHash()) < diff) {
            nonce++;
            l = new Lettre(pool.get(random), id, nonce);

        }
        pool.remove(random);
        sendMessage(new Message(l, id));
    }

    @Override
    public void sendMessage(Message m) {

        if (!getMessagesRecus().contains(m.getId())) {

            addMessageId(m.getId());
            switch (m.getType()) {
            case STRING:
                String message = m.getContenu();
                // System.out.println("AUTEUR : " + id + " receive " + message + " from " +
                // m.getAuteurID() + " ( MID : "
                // + m.getId() + " ) ");

                for (Pair pair : liens) {
                    if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                        pair.sendMessage(m);
                    }
                }
                break;

            case POOLLETTRE:

                this.pool = new ArrayList<>(m.getPool());

                // System.out.println("AUTEUR : " + id + " receive letter pool from " +
                // m.getAuteurID() + " ( MID : "
                // + m.getId() + " ) ");

                for (Pair pair : liens) {
                    if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                        pair.sendMessage(m);
                    }
                }

                synchronized (this) {

                    this.notifyAll();
                }

            case LETTRE:
                // System.out.println("AUTEUR : " + id + " receive " + m.getLettre().getC() + "
                // from " + m.getAuteurID()
                // + " ( MID : " + m.getId() + " ) ");

                for (Pair pair : liens) {
                    if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                        pair.sendMessage(m);
                    }
                }

                break;

            case BLOCK:

                // System.out.println(
                // "Auteur : " + id + " receive block from " + m.getAuteurID() + " ( MID : " +
                // m.getId() + " ) ");

                // if (m.getBlock().isValid()) {

                if (blockchain.size() < m.getBlock().size()) {
                    blockchain = m.getBlock();
                    for (Pair pair : liens) {

                        pair.sendMessage(new Message(id, blockchain));
                    }
                } else {

                    for (Pair pair : liens) {
                        if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                            pair.sendMessage(m);
                        }
                    }

                    // }
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

        while (!pool.isEmpty() && pool.size() > 0) {

            generateLettre();
        }

    }
}