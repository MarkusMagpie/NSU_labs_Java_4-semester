package task1;

public class Main {
    public static void main(String[] args) {
        String file = "task1/src/main/resources/input.txt";

//        WordFrequencyAnalyzer analyzer = new WordFrequencyAnalyzer();
        WordFrequencyAnalyzer.main(new String[]{file});
    }
}
