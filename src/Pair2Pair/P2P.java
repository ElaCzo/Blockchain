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

        Pair[] pairs = new Pair[40];

        Random rnd = new Random();

        ArrayList<Character> pool = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            pool.add((char) (rnd.nextInt(26) + 'a'));
        }

        pairs[0] = new Auteur(pool);

        for (int i = 1; i < pairs.length; i++) {
            if (i < 20) {
                pairs[i] = new Auteur();
            } else {
                pairs[i] = new Politicien(dict);

            }
        }

        for (int i = 0; i < pairs.length; i++) {
            int rand = rnd.nextInt(40);
            pairs[i].addLien(pairs[rand]);
            rand = rnd.nextInt(40);
            pairs[i].addLien(pairs[rand]);
            rand = rnd.nextInt(40);
            pairs[i].addLien(pairs[rand]);
            rand = rnd.nextInt(40);
            pairs[i].addLien(pairs[rand]);
            rand = rnd.nextInt(40);
            pairs[i].addLien(pairs[rand]);
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
    }

}