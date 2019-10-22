package Pair2Pair;

import java.util.ArrayList;

/**
 * P2P
 */

import java.util.Random;

public class P2P {

    public static void main(String[] args) {
        Pair[] pairs = new Pair[40];

        ArrayList<Character> pool = new ArrayList<>();
        pool.add('e');
        pool.add('d');
        pool.add('g');
        pool.add('f');

        pairs[0] = new Auteur(pool);

        for (int i = 1; i < pairs.length; i++) {
            if (i < 20) {
                pairs[i] = new Auteur();
            } else {
                pairs[i] = new Politicien();

            }
        }

        for (int i = 0; i < pairs.length; i++) {
            int rand = new Random().nextInt(19);
            pairs[i].addLien(pairs[rand]);
            rand = new Random().nextInt(19);
            pairs[i].addLien(pairs[rand]);
            rand = new Random().nextInt(19);
            pairs[i].addLien(pairs[rand]);
            rand = new Random().nextInt(19);
            pairs[i].addLien(pairs[rand]);
            rand = new Random().nextInt(19);
            pairs[i].addLien(pairs[rand]);
        }

        for (Pair pair :pairs ) {
            pair.start();
        }

        for (Pair pair : pairs) {
            try {
                pair.join();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

}