package Client;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class RandomWords {
    private static ArrayList<String> words = new ArrayList<>();
    private static int size;
    static {
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader("assignment2/words.txt"));
            String newLine;
            while((newLine = fileReader.readLine()) != null) {
                words.add(newLine);
            }
            size = words.size();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static String getRandomWord() {
        return words.get(ThreadLocalRandom.current().nextInt(0, size));
    }
}
