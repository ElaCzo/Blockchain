package Pair2Pair;

import java.util.ArrayList;

/**
 * Auteur
 */

public class Politicien extends Pair {

    ArrayList<Lettre> lettres;
    ArrayList<String> dict;

    public Politicien(ArrayList<String> dict) {
        super();
        lettres = new ArrayList<>();
        this.dict = new ArrayList<>(dict);
    }

    public synchronized void addLettre(Lettre l) {
        if (!this.lettres.contains(l)) {
            lettres.add(l);
        }
    }

    @Override
    public void sendMessage(Message m) {

        addMessageId(m.getId());

        if (m.getType() == TypeMessage.STRING) {
            String message = m.getContenu();
            System.out.println("POLITICIEN : " + id + " receive " + message + " from " + m.getAuteurID() + " ( MID : "
                    + m.getId() + " ) ");

            for (Pair pair : liens) {
                if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                    pair.sendMessage(m);
                }
            }
        }

        if (m.getType() == TypeMessage.POOLLETTRE) {

            for (Pair pair : liens) {
                if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {
                    pair.sendMessage(m);
                }
            }

        }

        if (m.getType() == TypeMessage.LETTRE) {
            System.out.println("POLITICIEN : " + id + " receive " + m.getLettre().getC()
            + " from " + m.getAuteurID()
            + " ( MID : " + m.getId() + " ) ");

            addLettre(m.getLettre());

            for (Pair pair : liens) {
                if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                    pair.sendMessage(m);
                }
            }
        }

    }

    @Override
    public void run() {
        System.out.println("POLITICIEN : " + id + " start");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            // TODO: handle exception
        }

        // for (int i = 0; i < lettres.size(); i++) {
        //     System.out.println("lettre : " + lettres.get(i).getC());
        // }

    }
}