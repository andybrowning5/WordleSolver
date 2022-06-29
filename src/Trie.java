import java.util.ArrayList;
import java.util.Random;
public class Trie {
    //Object Variables
    public static final int ALPHABET = 26;
    char c;
    Trie[] children;
    Trie parent;
    boolean word;
    int timesUsed;

    //Constructor
    public Trie() {
        this.c = 0;
        this.children = new Trie[26];
        this.word = false;
    }

    //Basic Trie Functions
    public void add(String s) {
        if (s.isEmpty()) {
            this.word = true;
            return;
        }

        char letter = s.charAt(0);
        int index = letter - 'a';

        if (this.children[index] == null) {
            this.children[index] = new Trie();
        }
        this.children[index].c = letter;
        this.children[index].parent = this;
        this.children[index].add(s.substring(1));
    }
    public void delete(String s) {
        if (s.length() != 0) {
            if (this.children[s.charAt(0) - 'a'] != null) {
                this.children[s.charAt(0) - 'a'].delete(s.substring(1));
                this.children[s.charAt(0) - 'a'].word = false;
            }
        }
        //System.out.println("Deleted: " + s);
        this.cleanup();
    }
    public void cleanup() {
        for (int i = 0; i < ALPHABET; i++) {
            if (this.children[i] != null) {
                this.children[i].cleanup();
                if (!this.children[i].hasWord()) {
                    //System.out.println("Freed node with prefix: " + getPrefix(this.children[i]) + " (cleanup)");
                    this.children[i] = null;
                }
            }

        }
    }
    public boolean hasWord() {
        for (int i = 0; i < ALPHABET; i++) {
            if (this.children[i] != null) {
                if (this.children[i].hasWord()) {
                    return true;
                }
                if (this.children[i].word) {
                    return true;
                }
            }
        }
        return this.word;
    }
    public String getPrefix(Trie node) {
        StringBuilder prefix = new StringBuilder();
        while (node.parent != null) {
            prefix.append(node.c);
            node = node.parent;
        }
        prefix = new StringBuilder(new StringBuilder(prefix.toString()).reverse().toString());//reverses string
        return prefix.toString();
    }
    public void printAll(String buf, int level) {
        for (int i = 0; i < ALPHABET; i++) {
            if (this.children[i] != null) {
                buf = buf.substring(0, level) + (char) ('a' + i);
                if (this.children[i].word) {
                    System.out.println(buf);
                    this.children[i].printAll("", 0);
                }
                this.children[i].printAll(buf, level + 1);
            }
        }
    }
    public ArrayList<String> listify(Trie node){
        ArrayList<String> lis = new ArrayList<>();
        for(int i = 0; i < ALPHABET; i++){
            if(node.children[i] != null){
                if(node.children[i].word){
                    lis.add((getPrefix(node.children[i])));
                    //System.out.println(getPrefix(node.children[i]));
                }
                lis.addAll(node.children[i].listify(node.children[i]));
            }
        }
        return lis;
    }
    public int wordsInTrie(int count) {
        for (int i = 0; i < ALPHABET; i++) {
            if (this.children[i] != null) {
                count += this.children[i].wordsInTrie(0);
            }
            if (this.word) {
                return count + 1;
            }
        }
        return count;
    }

    //Wordle Eliminating Functions:
    public void eliminateGrey(String letters) {
        for (int i = 0; i < ALPHABET; i++) {
                for (int j = 0; j < letters.length(); j++) {
                    if (letters.charAt(j) >= 'a' && letters.charAt(j) <= 'z') {
                        this.children[letters.charAt(j) - 'a'] = null;
                    }
                }
            if (this.children[i] != null) {
                this.children[i].eliminateGrey(letters);
            }
        }
        //this.cleanup();
    }
    public void eliminateGreen(String letters, int level) {
        for (int j = 0; j < letters.length(); j++) {
            if (letters.charAt(j) >= 'a' && letters.charAt(j) <= 'z') {
                for (int k = 0; k < ALPHABET; k++) {
                    if (letters.charAt(j) - 'a' != k && level == j) {
                        this.children[k] = null;
                    }
                    if (this.children[k] != null) {
                        this.children[k].eliminateGreen(letters, level + 1);
                    }
                }
            }
        }

        //cleanup();
    }
    public void eliminateYellow(String letters, int level) {
        for (int i = 0; i < ALPHABET; i++) {
            if (this.children[i] != null) {
                this.children[i].eliminateYellow(letters, level + 1);
                if (level == 4) {
                    if (!yellowHelper(letters, getPrefix(this.children[i]))) {
                        this.children[i].word = false;
                    }
                }
            }
        }
        cleanup();
    }
    public boolean yellowHelper(String letters, String prefix) {
        boolean[] res;
        res = new boolean[letters.length()];

        for (int i = 0; i < prefix.length(); i++) {
            for (int j = 0; j < letters.length(); j++) {
                if (letters.charAt(j) < 'a' || letters.charAt(j) > 'z') {
                    res[j] = true;
                } else if (prefix.charAt(i) == letters.charAt(j) && j != i) {
                    res[j] = true;
                }
            }
        }
        for (int j = 0; j < letters.length(); j++) {
            if (!res[j]) {
                return false;
            }
        }
        return true;
    }

    //Frequency Analysis
    public void updateTimesUsed() {
        Trie node = this;
        for (int i = 0; i < ALPHABET; i++) {
            if (this.children[i] != null) {
                this.children[i].updateTimesUsed();
            }
            if (getPrefix(this).length() == 5) {
                while (node.parent != null) {
                    node.timesUsed++;
                    node = node.parent;
                }
            }
        }
    }

    //Guessing Algorithms
    public String randWord(int level, String prevGuess){
        ArrayList<Integer> lis = new ArrayList<>();
        Random rand = new Random();
        StringBuilder out = new StringBuilder();
        if(level >= 5){
            return out.toString();
        }

        for(int i = 0; i < ALPHABET; i++){
            if(this.children[i] != null){
                lis.add((this.children[i].c - 'a'));
            }
        }

        int index = lis.get(rand.nextInt(lis.size()));
        out.append(this.children[index].randWord(level + 1, prevGuess));
        out.append((char) (index + 'a'));
        if(level == 0){
            out.reverse();
        }
        if(!out.toString().equals(prevGuess)){
            randWord(level + 1, prevGuess);
        }

        return out.toString();
    }
}