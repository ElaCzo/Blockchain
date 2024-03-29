package Pair2Pair;

import java.util.ArrayList;

/**
 * Blockchain
 */
public class Blockchain {

    private ArrayList<Block> chain;
    private int bvalue;

    public Blockchain() {
        chain = new ArrayList<>();
        bvalue = 0;
    }

    public Blockchain(ArrayList<Block> bl) {
        chain = new ArrayList<>(bl);
    }

    public int getLastHash() {

        // System.out.println(chain.size());
        try {
            return chain.get(chain.size() - 1).getHash();

        } catch (IndexOutOfBoundsException e) {
            return 0;
        }
    }

    public int size() {
        return chain.size();
    }

    public boolean addBlock(Block blo) {
        bvalue = 0;
        blo.predhash = getLastHash();
        return chain.add(blo);

    }

    public boolean isValid() {
        if (size() > 21) {
            return false;
        }

        for (Block block : chain) {
            ArrayList<Integer> auteurs = new ArrayList<>();
            for (Lettre lettre : block.getLettres()) {
                if (lettre.getBlockhash() != block.predhash) {
                    return false;
                }
                if (auteurs.contains(lettre.getAuteurId())) {
                    return false;
                } else {
                    auteurs.add(lettre.getAuteurId());
                }

            }
        }

        return true;
    }

    public ArrayList<Character> getChars() {
        ArrayList<Character> chars = new ArrayList<>();

        for (Block block : chain) {
            for (Lettre l : block.getLettres()) {
                chars.add(l.getC());
            }
        }
        return chars;
    }

    public int value() {
        if (bvalue != 0) {
            return bvalue;
        }

        int value = 0;

        for (Block block : chain) {
            for (Lettre l : block.getLettres()) {
                value += scrableValue(l.getC());
            }
        }

        bvalue = value;
        return value;
    }

    public int scrableValue(char c) {
        int value = 0;
        switch (c) {
        case 'a':
            value += 1;
            break;
        case 'b':
            value += 3;
            break;
        case 'c':
            value += 3;
            break;
        case 'd':
            value += 2;
            break;
        case 'e':
            value += 1;
            break;
        case 'f':
            value += 4;
            break;
        case 'g':
            value += 2;
            break;
        case 'h':
            value += 4;
            break;
        case 'i':
            value += 1;
            break;
        case 'j':
            value += 8;
            break;
        case 'k':
            value += 10;
            break;
        case 'l':
            value += 1;
            break;
        case 'm':
            value += 2;
            break;

        case 'n':
            value += 1;
            break;
        case 'o':
            value += 1;
            break;

        case 'p':
            value += 3;
            break;

        case 'q':
            value += 8;
            break;

        case 'r':
            value += 1;
            break;

        case 's':
            value += 1;
            break;

        case 't':
            value += 1;
            break;

        case 'u':
            value += 1;
            break;

        case 'v':
            value += 4;
            break;

        case 'w':
            value += 10;
            break;

        case 'x':
            value += 10;
            break;

        case 'y':
            value += 10;
            break;

        case 'z':
            value += 10;
            break;

        default:
            break;
        }
        return value;
    }

    public int auteurScore(int aid) {

        int value = 0;

        for (Block block : chain) {
            for (Lettre l : block.getLettres()) {
                if (l.getAuteurId() == aid) {

                    value += scrableValue(l.getC());
                }
            }
        }
        bvalue = value;
        return value;

    }

    public int politicienScore(int pid) {

        int value = 0;

        for (Block block : chain) {
            if (block.getAuteurID() == pid) {
                for (Lettre l : block.getLettres()) {

                    value += scrableValue(l.getC());
                }
            }
        }
        bvalue = value;
        return value;

    }

    /**
     * @return the chain
     */
    public ArrayList<Block> getChain() {
        return chain;
    }

    @Override
    public String toString() {
        String sortie = new String();
        sortie += "[";
        for (Block block : chain) {
            sortie += block.toString();
        }
        sortie += "]";
        return sortie;
    }

}