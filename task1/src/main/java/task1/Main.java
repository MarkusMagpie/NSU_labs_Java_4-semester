package task1;

public class Main {
    public static void main(String[] args) {
        // pass the name of the input file as an argument
        String file = "task1\\src\\main\\java\\task1\\input.txt";

//        WordFrequencyAnalyzer analyzer = new WordFrequencyAnalyzer();
        WordFrequencyAnalyzer.main(new String[]{file});
    }
}
