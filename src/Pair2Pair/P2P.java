package Pair2Pair;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * P2P
 */

import java.util.Random;

public class P2P {

    public static void main(String[] args) {

        ArrayList<String> dict = new ArrayList<>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("./dict/dict_100000_1_10.txt"));
            String line = reader.readLine();
            while (line != null) {
                dict.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            reader = new BufferedReader(new FileReader("./dict/dict_100000_5_15.txt"));
            String line = reader.readLine();
            while (line != null) {
                dict.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            reader = new BufferedReader(new FileReader("./dict/dict_100000_25_75.txt"));
            String line = reader.readLine();
            while (line != null) {
                dict.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            reader = new BufferedReader(new FileReader("./dict/dict_100000_50_200.txt"));
            String line = reader.readLine();
            while (line != null) {
                dict.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int nbPairs = 10;

        Pair[] pairs = new Pair[nbPairs];

        Random rnd = new Random();

        ArrayList<Character> pool = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            pool.add((char) (rnd.nextInt(26) + 'a'));
        }

        pairs[0] = new Auteur(pool);

        for (int i = 1; i < nbPairs; i++) {
            if (i < nbPairs / 2) {
                pairs[i] = new Auteur();
            } else {
                pairs[i] = new Politicien(dict);

            }
        }

        for (int i = 0; i < nbPairs; i++) {
            if (i != 0) {
                pairs[i].addLien(pairs[i - 1]);
            }
            int rand = rnd.nextInt(nbPairs);
            pairs[i].addLien(pairs[rand]);
            rand = rnd.nextInt(nbPairs);
            pairs[i].addLien(pairs[rand]);
            rand = rnd.nextInt(nbPairs);
            pairs[i].addLien(pairs[rand]);
            rand = rnd.nextInt(nbPairs);
            pairs[i].addLien(pairs[rand]);
            rand = rnd.nextInt(nbPairs);
            pairs[i].addLien(pairs[rand]);
            if (i < nbPairs - 2) {
                pairs[i].addLien(pairs[i + 1]);
            }
        }

        for (Pair pair : pairs) {
            pair.start();
        }

        for (Pair pair : pairs) {
            try {
                pair.join();
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        for (Pair pair : pairs) {
            try {
                System.out.println("Pair : " + pair.getPairId() + " score : " + pair.getScore());
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

}