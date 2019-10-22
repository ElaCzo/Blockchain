package Pair2Pair;

import java.util.ArrayList;

/**
 * Auteur
 */

public class Politicien extends Pair {

    public Politicien() {
        super();
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

    }

    public void run() {

        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            // TODO: handle exception
        }

        System.out.println("POLITICIEN : " + id + " start");
    }
}