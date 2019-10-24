package Pair2Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Random;

/**
 * Auteur
 */

public class Politicien extends Pair {

    private ArrayList<ArrayList<Block>> received_block;

    private ArrayList<Lettre> lettres;
    private ArrayList<Lettre> lettresRec;
    private ArrayList<String> dict;

    private int diff = 24;

    public Politicien(ArrayList<String> dict) {
        super();
        received_block = new ArrayList<>();
        this.lettres = new ArrayList<>();
        this.lettresRec = new ArrayList<>();

        this.dict = new ArrayList<>(dict);
        Collections.shuffle(dict);
    }

    public synchronized void addLettre(Lettre l) {
        if (!this.lettresRec.contains(l)) {
            synchronized (lettres) {
                lettres.add(l);
                lettresRec.add(l);
            }

        }
    }

    public void calcScore() {

        for (Block blo : blockchain) {

            if (blo.getAuteurID() == id) {

                synchronized ((Integer) score) {
                    score += blo.getLettres().size();
                }
            }

        }

    }

    @Override
    public int getScore() {
        calcScore();
        return score;
    }

    public Block createMot() {
        if (lettres.size() > 0) {

            synchronized (lettres) {

                String mot = new String("");
                ArrayList<Lettre> lmot = new ArrayList<>();

                for (String dmot : dict) {

                    for (char c : dmot.toCharArray()) {
                        Lettre l = null;

                        for (Lettre lettre : lettres) {

                            if (c == lettre.getC() && !Lettre.contain(lettre.getAuteurId(), lmot)) {
                                l = lettre;
                                break;
                            }
                        }
                        if (l != null) {
                            lmot.add(l);
                        }

                    }
                    if (dmot.length() == lmot.size()) {
                        mot = dmot;
                        break;
                    } else {
                        lmot.clear();
                    }
                }

                int nonce = 0;

                if (mot.length() == 0 || lmot.size() == 0) {
                    return null;
                }

                Block b = new Block(mot, lmot, getPairId(), nonce);

                while (Integer.numberOfLeadingZeros(b.getHash()) < diff) {

                    nonce++;
                    b = new Block(mot, lmot, getPairId(), nonce);
                }

                return b;
            }

        }

        return null;

    }

    @Override
    public void sendMessage(Message m) {

        if (!getMessagesRecus().contains(m.getId())) {

            addMessageId(m.getId());

            switch (m.getType()) {
            case STRING:
                String message = m.getContenu();
                // System.out.println("POLITICIEN : " + id + " receive " + message + " from " +
                // m.getAuteurID()
                // + " ( MID : " + m.getId() + " ) ");

                for (Pair pair : liens) {
                    if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                        pair.sendMessage(m);
                    }
                }
                break;

            case POOLLETTRE:

                for (Pair pair : liens) {
                    if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {
                        pair.sendMessage(m);
                    }
                }
                break;

            case LETTRE:
                // System.out.println("POLITICIEN : " + id + " receive " + m.getLettre().getC()
                // + " from "
                // + m.getAuteurID() + " ( MID : " + m.getId() + " ) ");

                synchronized (lettres) {

                    addLettre(m.getLettre());

                }

                for (Pair pair : liens) {
                    if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                        pair.sendMessage(m);
                    }
                }

                synchronized (this) {
                    this.notifyAll();
                }

                break;

            case BLOCK:

                // System.out.println("POLITICIEN : " + id + " receive block from " +
                // m.getAuteurID() + " ( MID : "
                // + m.getId() + " ) ");

                synchronized (received_block) {

                    received_block.add(m.getBlock());
                }

                // for (Pair pair : liens) {
                // if (m.getAuteurID() != pair.getPairId() &&
                // !pair.getMessagesRecus().contains(m.getId())) {

                // pair.sendMessage(m);
                // }
                // }

                break;

            default:
                break;
            }
        }

    }

    @Override
    public void run() {
        System.out.println("POLITICIEN : " + id + " start");

        while (lettres.size() < 10) {

            try {

                synchronized (this) {
                    this.wait();
                }

            } catch (Exception e) {
                // TODO: handle exception
            }

        }

        while (!lettres.isEmpty() || lettres.size() > 0) {

            Block b = createMot();
            System.out.println("POLITICIEN : " + id + " a fini Block | taille du mot " + b.getMot().length() + "/"
                    + b.getLettres().size());

            if (b != null) {

                blockchain.add(b);
            }

            // if (!b.isValid()) {
            // blockchain = new Block();
            // }

            synchronized (received_block) {
                for (ArrayList<Block> block : received_block) {
                    assert (block != null);
                    if (block.size() > blockchain.size()) {
                        blockchain = block;
                    }
                }

                received_block.clear();
            }

            synchronized (lettres) {

                lettres = new ArrayList<>(lettresRec);

                ArrayList<Lettre> l = new ArrayList<>();

                for (Block block : blockchain) {
                    l.addAll(block.getLettres());

                }

                lettres.removeAll(l);

            }

            for (Pair pair : liens) {

                pair.sendMessage(new Message(id, blockchain));
            }

            System.out.println("POLITICIEN : " + id + " taille Block " + blockchain.size() + " | lettres restantes "
                    + lettres.size() + "/" + lettresRec.size());
            // blockchain.print();

        }

        System.out.println("POLITICIEN : " + id + " a fini | B taille : " + blockchain.size());

    }
}