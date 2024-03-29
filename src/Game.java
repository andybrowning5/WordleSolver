import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
public class Game {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Would you like to play one game, or simulate multiple? (enter 1 for one game, 2 for multiple): ");
        int a = sc.nextInt();
        if(a == 1){
            oneGame();
        }else if(a == 2){
            System.out.println("Would you like to iterate through all words (press 1) or iterate a set number of random words (press 2)?");
            a = sc.nextInt();
            if(a == 1){
                iterateSimsAllWords();
            }else if(a == 2){
                System.out.println("How many iterations would you like to run?: ");
                int iterations = sc.nextInt();
                iterateSimsRand(iterations);

            }
        }
    }

    public static void oneGame() throws FileNotFoundException {
        Trie dictionary = initializeTrie();
        Scanner sc = new Scanner(System.in);
        String t;
        String guess;
        String prevGuess = "";

        System.out.println("Enter 'raise' as your first guess");
        for(int i = 0; i < 7; i++){
            System.out.println("Enter grey letters (use any non letter character as spacers): ");
            t = sc.nextLine();
            dictionary.eliminateGrey(t);
            System.out.println("Enter green letters(use any non letter character as spacers): ");
            t = sc.nextLine();
            dictionary.eliminateGreen(t, 0);
            System.out.println("Enter yellow letters(use any non letter character as spacers): ");
            t = sc.nextLine();
            dictionary.eliminateYellow(t, 0);

            if(dictionary.wordsInTrie(0) <= 2){
                guess = dictionary.randWord(0, prevGuess);
            }else{
                guess = testForAll(dictionary.listify(dictionary));
            }
            System.out.println("The best next guess is: " + guess);
            prevGuess = guess;
        }
    }
    public static void iterateSimsRand(int iterations) throws FileNotFoundException {
        NumberFormat formatter = new DecimalFormat("#0.000");
        final long startTime = System.currentTimeMillis();
        double totalTurns = 0.0;
        int[] dist = new int[7];

        for(int i = 0; i < iterations; i++) {
            System.out.println();
            System.out.println("NEW GAME: " + i);
            Trie dictionary = initializeTrie();
            String word = pickRandomWord();
            String greenletters = "";
            int turns = newTurn(dictionary, 1, word, "", greenletters);
            dist[turns - 1]++;
            totalTurns += turns;
        }

        for(int i = 0; i < 7; i++){
            if(i == 6){
                System.out.println("distribution: Didnt finish: " + dist[i]);
            }else{
                System.out.println("distribution: " + (i + 1) + " turns occurred: " + dist[i]);
            }
        }

        double averageTurns = totalTurns/iterations;
        System.out.println("Average amount of turns over " + iterations + " iterations, using test for all ALG: " + averageTurns);


        System.out.println("With a standard deviation of: " + formatter.format(findDeviation(averageTurns, dist, iterations)));

        final long endTime = System.currentTimeMillis();

        System.out.println("Finished in: " + (int)((endTime - startTime)/1000.0/60) + ":"+ (int)((endTime - startTime)/1000.0)%60);
    }
    public static void iterateSimsAllWords() throws FileNotFoundException {
        int iterations = 2309;
        ArrayList<String> words = new ArrayList<>();
        File file = new File("src/answerWords.txt");
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            words.add(sc.nextLine());
        }


        NumberFormat formatter = new DecimalFormat("#0.000");
        final long startTime = System.currentTimeMillis();
        double totalTurns = 0.0;
        int[] dist = new int[7];

        for(int i = 0; i < iterations; i++) {
            System.out.println();
            System.out.println("NEW GAME: " + i);
            Trie dictionary = initializeTrie();
            String word = words.get(i);
            String greenletters = "";
            int turns = newTurn(dictionary, 1, word, "", greenletters);
            dist[turns - 1]++;
            totalTurns += turns;
        }

        for(int i = 0; i < 7; i++){
            if(i == 6){
                System.out.println("distribution: Didnt finish: " + dist[i]);
            }else{
                System.out.println("distribution: " + (i + 1) + " turns occurred: " + dist[i]);
            }
        }

        double averageTurns = totalTurns/iterations;
        System.out.println("Average amount of turns over " + iterations + " iterations, using test for all ALG: " + averageTurns);


        System.out.println("With a standard deviation of: " + formatter.format(findDeviation(averageTurns, dist, iterations)));

        final long endTime = System.currentTimeMillis();

        System.out.println("Finished in: " + (int)((endTime - startTime)/1000.0/60) + ":"+ (int)((endTime - startTime)/1000.0)%60);
    }

    private static Trie initializeTrie() throws FileNotFoundException {
        Trie dictionary = new Trie();
        File file = new File("src/answerWords.txt");
        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            dictionary.add(sc.nextLine());
        }

        return dictionary;
    }
    public static int newTurn(Trie dictionary, int turn, String word, String prevGuess, String greenletters) throws FileNotFoundException {
        System.out.println("_____________________");
        System.out.println("Turn: " + turn);
        System.out.println(dictionary.listify(dictionary));
        System.out.println(dictionary.wordsInTrie(0) + " words left");

        String guess;

        if(turn == 1){
            guess = "roate";
        }else {
            if (dictionary.wordsInTrie(0) > 1) {
                dictionary.delete(prevGuess);
                guess = testForAll(dictionary.listify(dictionary));
            }else{
                guess = word;
            }
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
        File file = new File("src/answerWords.txt");
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            words.add(sc.nextLine());
        }
        return words.get(rand.nextInt(words.size()));
    }
    public static double findDeviation(double average, int[] dist, int iterations){
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

        File file = new File("src/guessWords.txt");
        Scanner sc = new Scanner(file);
        int[] lowest = new int[2];
        while (sc.hasNextLine()) {
            words.add(sc.nextLine());
            wordCount.add(0);
        }
        Simulator sim = new Simulator();
        lowest[0] = Integer.MAX_VALUE;

        for(int i = 0; i < words.size(); i++){
            //System.out.println(i + " " + words.get(i));
            for(int j = dicList.size()-1; j > 0; j--){
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
            //tie
            for (String s : tie) {
                for (String value : dicList) {
                    if (s.equals(value)) {
                        return s;
                    }
                }
            }
        }
        return words.get(lowest[1]);
    }
}