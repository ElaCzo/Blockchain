package Pair2Pair;

import java.util.ArrayList;
import java.util.Random;

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
        this.pool = new ArrayList<>(pool);
    }

    /**
     * @return the pool
     */
    public ArrayList<Character> getPool() {
        return pool;
    }

    private void generateLettre() {

        Random rnd = new Random();
        int random = rnd.nextInt(pool.size() );
        int nonce = 0;
        Lettre l = new Lettre(pool.get(random), id, nonce);
        while (Integer.numberOfLeadingZeros(l.getHash()) < 8) {
            nonce++;
            l = new Lettre(pool.get(random), id, nonce);

        }
        pool.remove(random);
        sendMessage(new Message(l, id));
    }

    @Override
    public void sendMessage(Message m) {

        addMessageId(m.getId());

        if (m.getType() == TypeMessage.STRING) {
            String message = m.getContenu();
            // System.out.println("AUTEUR : " + id + " receive " + message + " from " +
            // m.getAuteurID() + " ( MID : "
            // + m.getId() + " ) ");

            for (Pair pair : liens) {
                if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                    pair.sendMessage(m);
                }
            }
        }

        if (m.getType() == TypeMessage.POOLLETTRE) {

            pool = this.pool = new ArrayList<>(m.getPool());

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
        }

        if (m.getType() == TypeMessage.LETTRE) {
            // System.out.println("AUTEUR : " + id + " receive " + message + " from " +
            // m.getAuteurID() + " ( MID : "
            // + m.getId() + " ) ");

            for (Pair pair : liens) {
                if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                    pair.sendMessage(m);
                }
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

        while (!pool.isEmpty() && pool.size() > 0) {
            generateLettre();
        }
        System.out.println("AUTEUR : " + id + " a fini");
    }
}