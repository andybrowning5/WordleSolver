import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
public class Game {
    final static int iterations = 100;

    public static void main(String[] args) throws FileNotFoundException {
        NumberFormat formatter = new DecimalFormat("#0.000");
        final long startTime = System.currentTimeMillis();
        double totalTurns = 0.0;
        int[] dist = new int[7];

        for(int i = 0; i < iterations; i++) {
            long startGameTime = System.nanoTime();
            System.out.println();
            System.out.println("NEW GAME: " + i);
            Trie dictionary = initializeTrie();
            String word = pickRandomWord();
            String greenletters = "";
            int turns = newTurn(dictionary, 1, word, "", greenletters);
            dist[turns - 1]++;
            totalTurns += turns;
            long averageTurns = (long) (totalTurns/(i + 1));
            System.out.println("Average Turns: " + averageTurns);
            double endGameTime = System.nanoTime();
            System.out.println("This Game took: " + (endGameTime - startGameTime/1000000) + " seconds to execute");
            System.out.println();
        }

        for(int i = 0; i < 7; i++){
            if(i == 6){
                System.out.println("distribution: Didnt finish: " + dist[i]);
            }else{
                System.out.println("distribution: " + (i + 1) + " turns occurred: " + dist[i]);
            }
            //totalTurns += (dist[i]) * (i + 1);
        }

        double averageTurns = totalTurns/iterations;
        System.out.println("Average amount of turns over " + iterations + " iterations, using test for all ALG: " + averageTurns);


        System.out.println("With a standard deviation of: " + formatter.format(findDeviation(averageTurns, dist)));

        final long endTime = System.currentTimeMillis();

        System.out.println("Finished in: " + ((endTime - startTime)/1000.0) + " Seconds");
    }
    private static Trie initializeTrie() throws FileNotFoundException {
        Trie dictionary = new Trie();
        File file = new File("src/Dictionary.txt");
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            dictionary.add(sc.nextLine());
        }

        return dictionary;
    }
    public static int newTurn(Trie dictionary, int turn, String word, String prevGuess, String greenletters) throws FileNotFoundException {
        System.out.println("_____________________");
        System.out.println("Turn: " + turn);
        //System.out.println(dictionary.listify(dictionary));
        //System.out.println(dictionary.wordsInTrie(0) + " words in trie");
        //sets Algorithm
        String guess;
        guess = "";

        if(turn == 1){
            guess = "raise";
        }else {
            if (dictionary.wordsInTrie(0) > 1) {
                guess = testForAll(dictionary.listify(dictionary));
                //guess = mostFreqLettersNoGreens(dictionary, prevGuess, greenletters);
            }
        }
        if(dictionary.wordsInTrie(0) <= 2){
            guess = dictionary.randWord(0, prevGuess);
        }
        if(dictionary.wordsInTrie(0) <= 1){
            guess = word;
        }

        Simulator sim = new Simulator();
        sim.runSimulator(guess, word);
        greenletters += sim.greenOutput(guess, word);

        dictionary.eliminateGrey(String.valueOf(sim.greyOutput(guess, word)));
        dictionary.eliminateGreen(String.valueOf(sim.greenOutput(guess, word)), 0);
        dictionary.eliminateYellow(String.valueOf(sim.yellowOutput(guess, word)), 0);
        dictionary.updateTimesUsed();

        if(turn >= 7){
            return turn;
        }
        if(!guess.equals(word)) {
            turn = newTurn(dictionary, turn + 1, word, guess, greenletters);
        }
        return turn;
    }

    public static String pickRandomWord() throws FileNotFoundException {
        ArrayList<String> words = new ArrayList<>();
        Random rand = new Random();
        File file = new File("src/Dictionary.txt");
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            words.add(sc.nextLine());
        }

        return words.get(rand.nextInt(words.size()));
    }
    public static double findDeviation(double average, int[] dist){
        double standardDeviation = 0;
        for(int i = 0; i < dist.length; i++){
            standardDeviation += Math.pow((double)(i + 1) - average, 2) * dist[i];
        }
        standardDeviation /= iterations;
        standardDeviation = Math.sqrt(standardDeviation);
        return standardDeviation;
    }
    public static String testForAll(ArrayList<String> dicList) throws FileNotFoundException {
        ArrayList<String> words = new ArrayList<>();
        ArrayList<Integer> wordCount = new ArrayList<>();
        ArrayList<String> tie = new ArrayList<>();

        File file = new File("src/Dictionary.txt");
        Scanner sc = new Scanner(file);
        int[] lowest = new int[2];
        while (sc.hasNextLine()) {
            words.add(sc.nextLine());
            wordCount.add(0);
        }
        Simulator sim = new Simulator();
        lowest[0] = 10000000;

        for(int i = 0; i < words.size(); i++){
            for(int j = 0; j < dicList.size(); j++){
                Trie test = new Trie();
                for (String s : dicList) {
                    test.add(s);
                }
                test.eliminateGrey(sim.greyOutput(words.get(i), dicList.get(j)));
                test.eliminateGreen(sim.greenOutput(words.get(i), dicList.get(j)), 0);
                test.eliminateYellow(sim.yellowOutput(words.get(i), dicList.get(j)), 0);
                wordCount.set(i, wordCount.get(i) + test.wordsInTrie(0));
                if(wordCount.get(i) > lowest[0]){
                    break;
                }
            }
            if(wordCount.get(i) < lowest[0]){
                lowest[0] = wordCount.get(i);
                lowest[1] = i;
                tie.clear();
            }else if(wordCount.get(i) == lowest[0]){
                tie.add(words.get(i));
            }
        }
        if(tie.size() > 0){
            System.out.println("WE HAD A TIE");
            for (String s : tie) {
                for (String value : dicList) {
                    if (s.equals(value)) {
                        return s;
                    }
                }
            }
        }
        System.out.println(words.get(lowest[1]) + "  " + wordCount.get(lowest[1]) + " Word Guessed");
        return words.get(lowest[1]);
    }
}