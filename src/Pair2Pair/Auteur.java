package Pair2Pair;

import java.util.ArrayList;

/**
 * Auteur
 */
public class Auteur extends Pair {

    private ArrayList<Character> pool;

    public Auteur() {
        super();
        this.pool = null;
    }

    public Auteur(ArrayList<Character> pool) {
        super();
        this.pool = pool;
    }

    /**
     * @return the pool
     */
    public ArrayList<Character> getPool() {
        return pool;
    }

    @Override
    public void sendMessage(Message m) {

        addMessageId(m.getId());

        if (m.getType() == TypeMessage.STRING) {
            String message = m.getContenu();
            // System.out.println("AUTEUR : " + id + " receive " + message + " from " + m.getAuteurID() + " ( MID : "
            //         + m.getId() + " ) ");

            for (Pair pair : liens) {
                if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                    pair.sendMessage(m);
                }
            }
        }

        if (m.getType() == TypeMessage.POOLLETTRE) {

            pool = m.getPool();

            // System.out.println("AUTEUR : " + id + " receive letter pool from " + m.getAuteurID() + " ( MID : "
            //         + m.getId() + " ) ");

            for (Pair pair : liens) {
                if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                    pair.sendMessage(m);
                }
            }

            synchronized (this) {

                this.notifyAll();
            }
        }

    }

    public void run() {
        try {
            Thread.sleep(1000);
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
    }
}