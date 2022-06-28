public class Simulator {
    public void runSimulator(String guess, String word){
        System.out.println("Guess: " + guess);

        System.out.println("Grey Letters:   " + greyOutput(guess, word));
        System.out.println("Green Letters:  " + greenOutput(guess, word));
        System.out.println("Yellow Letters: " + yellowOutput(guess, word));
    }

    public String greyOutput(String guess, String word){
        StringBuilder out = new StringBuilder();
        boolean flag;
        for(int i = 0; i < guess.length(); i++){
            flag = false;
            for(int j = 0; j < word.length(); j++){
                if(guess.charAt(i) == word.charAt(j)) {
                    flag = true;
                    break;
                }
            }
            if(!flag){
                out.append(guess.charAt(i));
            }else{
                out.append("-");
            }
        }
        return out.toString();
    }
    public String greenOutput(String guess, String word){
        StringBuilder out = new StringBuilder();
        for(int i = 0; i < word.length(); i++){
            if(guess.charAt(i) == word.charAt(i)){
                out.append(guess.charAt(i));
            }else{
                out.append("-");
            }
        }
        return out.toString();
    }
    public String yellowOutput(String guess, String word){
        StringBuilder out = new StringBuilder();
        boolean flag;
        for(int i = 0; i < word.length(); i++){
            flag = false;
            if(guess.charAt(i) != word.charAt(i)){
                for(int j = 0; j < word.length(); j++){
                    if (guess.charAt(i) == word.charAt(j)) {
                        flag = true;
                        break;
                    }
                }
            }
            if(flag){
                out.append(guess.charAt(i));
            }else{
                out.append("-");
            }
        }
        return out.toString();
    }
}