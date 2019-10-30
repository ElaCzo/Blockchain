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

    private ArrayList<Blockchain> received_block;

    private ArrayList<Lettre> lettres;
    private ArrayList<String> dict;

    private int diff = 16;

    public Politicien(ArrayList<String> dict) {
        super();
        received_block = new ArrayList<>();
        this.lettres = new ArrayList<>();

        this.dict = new ArrayList<>(dict);
        Collections.shuffle(dict);
    }

    public synchronized void addLettre(Lettre l) {
        if (!this.lettres.contains(l) && l.getBlockhash() == blockchain.getLastHash()) {
            synchronized (lettres) {
                lettres.add(l);
            }
        }
    }

    public int getScore() {
        return blockchain.politicienScore(id);
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

                            if (c == lettre.getC() && !Lettre.contain(lettre.getAuteurId(), lmot)
                                    && lettre.getBlockhash() == blockchain.getLastHash()) {
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

            if (fini) {
                // for (Pair pair : liens) {
                // if (m.getAuteurID() != pair.getPairId() &&
                // !pair.getMessagesRecus().contains(m.getId())) {

                // pair.sendMessage(new Message(blockchain));

                // }

                // }
                return;

            }

            switch (m.getType()) {
            case STRING:
                String message = m.getContenu();
                System.out.println("POLITICIEN : " + id + " receive " + message + " from " + m.getAuteurID()
                        + " ( MID : " + m.getId() + " ) ");

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

                break;

            case BLOCK:

                if (fini) {
                    for (Pair pair : liens) {
                        if (m.getAuteurID() != pair.getPairId() && !pair.getMessagesRecus().contains(m.getId())) {

                            pair.sendMessage(new Message(blockchain));

                        }

                    }
                    return;

                } else {

                    synchronized (received_block) {

                        received_block.add(m.getBlock());
                    }
                }

                break;

            case FINI:

                System.out.println("POLITICIEN : " + id + " receive Fin from " + m.getAuteurID() + " nb lettres "
                        + m.getBlock().getChars().size() + " ( MID : " + m.getId() + " ) ");

                if (m.getBlock().isValid()) {
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
            synchronized (this) {
                // System.out.println("POLITICIEN : " + id + " wakeup lettre");

                this.notifyAll();
            }
        }

    }

    @Override
    public void run() {
        System.out.println("POLITICIEN : " + id + " start");

        while (!fini) {

            Blockchain blo = null;

            if (blockchain.size() < 20 && lettres.size() >7) {

                Block b = createMot();

                System.out.println("POLITICIEN : " + id + " a fait Block | size: " + blockchain.size());

                if (b != null) {

                    blo = new Blockchain(blockchain.getChain());
                    blo.addBlock(b);

                }
            }

            synchronized (received_block) {
                if ( blo!= null && blo.isValid()) {
                    received_block.add(blo);
                }

                for (Blockchain block : received_block) {
                    assert (block != null);
                    if (block.isValid() && block.value() > blockchain.value()) {
                        blockchain = block;
                    }
                }

                received_block.clear();
            }

            synchronized (lettres) {

                lettres.clear();

            }
            for (Pair pair : liens) {
                pair.sendMessage(new Message(new Blockchain(blockchain.getChain()), id));
            }

            // System.out.println("POLITICIEN : " + id + " taille Block " +
            // blockchain.getChain().size() + " nb lettres :"
            // + blockchain.getChars().size() + " value " + blockchain.value());

        }

        if (!fini) {

            synchronized (this) {

                // System.out.println("POLITICIEN : " + id + " attend lettre");
                try {

                    this.wait();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }

        } else {
            System.out.println("POLITICIEN : " + id + " a fini | B taille : " + blockchain.getChain().size());

            return;
        }

        System.out.println("POLITICIEN : " + id + " a fini | B taille : " + blockchain.size());

    }
}